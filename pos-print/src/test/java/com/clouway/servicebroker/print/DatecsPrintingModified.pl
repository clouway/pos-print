#!/usr/bin/perl -w
#
use strict;
use warnings;
use Device::SerialPort ;
use IO::Socket;

# Globals

my $last_command = 0;
my $last_index = 0;
my $verbose = 0;
my $port = "/dev/ttyS0"; 	# You need to set this manually with symlink to the serial port where, 
				# the fp3530 is set if you are not executinh thse script as root.
				# if you are root, you can change this line to your actual serial port.

sub encode {
# Function encode (sequence, command, data)
# sequence is between 0x20 and 0x7f
# command is described in the printer's manual
# data depends on command and can be OMITED
# encoded message must be <01><LEN><SEQ><CMD><"DATA"><05><BCC><03>
	my $seq = shift;
	my $cmd = shift;
	my $data = shift;
# Cyrilize old style if needed   the printer has Cyrilic starting from 0x80 ... must be 0xc0 to show properly
	for (my $i = 0; $i < length($data); $i++ ) {
		if (ord(substr($data,$i,1)) > 0xbf) { substr($data,$i,1) = chr(ord(substr($data,$i,1))-0x40);}
	}
	my $bcc = 0;						#bcc is the sum of all bytes after #01 including 0x05
	$bcc += ord($seq);
	$bcc += ord($cmd);
	for (my $i = 0; $i < length($data) ; $i++) {
		$bcc += ord(substr($data, $i, 1));
	}
	$bcc += 0x05;						# 0x05 is data terminator
	my $lng = 32 + 4 + length($data);			# second byte from the message is the length of 
	$bcc += $lng;						# bytes between 0x01(excl) and 0x05(incl) plus 0x20
	my $b1 = chr(0x30 + ($bcc - ($bcc % 4096)) / 4096);	# after calculating BCC must be *encoded*
	my $hlp = $bcc % 4096;					# if Checksum is 0x1234 then BCC must be 0x31 0x32 0x33 0x34
	my $b2 = chr(0x30 + ($hlp - ($hlp % 256)) / 256);
	$hlp = $bcc % 256;
	my $b3 = chr(0x30 + ($hlp - ($hlp % 16)) / 16);
	$hlp = $bcc % 16;
	my $b4 = chr(0x30 + $hlp);
	my $len = chr($lng);
	my $encoded = chr(0x01) . $len . $seq . $cmd . $data . chr(0x05) . $b1 . $b2 . $b3 . $b4 . chr(0x03) ; # <-- completed message
# Debuging
	if ($verbose) {
                print $encoded;
                print "\n";
		my $encoded_textual = "";
		for (my $i = 0; $i < length($encoded); $i++) {
			$encoded_textual = $encoded_textual . sprintf("%02x", ord(substr($encoded,$i,1))) . "";
		}
		printf ("Encoded message : $encoded_textual\n");
	}
# end of Debuging 
	return ($encoded);
}

sub prn_reply {
	my $msg = shift;
	$last_command = ord(substr($msg,3,1));
	$last_index = ord(substr($msg,2,1));
	if ($verbose) {
		if ($msg ne "") {
			print ("Prn replied with: ");
			for (my $i = 0; $i < length($msg); $i++) {
				my $tmp = sprintf("%02x", ord(substr($msg, $i, 1)));
				if ($tmp ne "16") {
					print ($tmp . "");
				}
			}
		}
		print ("($msg)\n");

		print ("Command was : $last_command\n Index was: $last_index\n");
	}
# strip the 6 status bytes from reply message
	my $flag = "";
	my @sb;
	my $j = 0;
	for (my $i = 0; $i < length($msg); $i++) {
		my $tmp = ord(substr($msg, $i, 1));
		if ($tmp == 0x05) { $flag = "0"}
		if ($flag) {
			$sb[$j] = sprintf("%08b", $tmp);
			if ($verbose) {print("Status bits $j are ".$sb[$j],"\n");}
			$j++;
		}
		if ($tmp == 0x04) { $flag = "1"}
	}
# FATAL ERRORS
	if (substr($sb[0],2,1) eq "1") { print("Fatal Error :\n"); }
	if (substr($sb[0],3,1) eq "1") { print(" Mechanics Error !\n"); }
	if (substr($sb[0],6,1) eq "1") { print(" Invalid OPcode !\n"); }
	if (substr($sb[0],7,1) eq "1") { print(" DATA Syntax error !\n"); }
	if (substr($sb[1],3,1) eq "1") { print(" MEMORY CORRUPT !\n"); }
	if (substr($sb[1],4,1) eq "1") { print(" Print Canceled !\n"); }
	if (substr($sb[1],5,1) eq "1") { print(" MEMORY Cleared !\n"); }
	if (substr($sb[1],6,1) eq "1") { print(" Command not allowed in current fiscal mode !\n"); }
	if (substr($sb[2],7,1) eq "1") { print(" NO PAPER !\n"); }
	if (substr($sb[4],2,1) eq "1") { print("MEMORY ERROR :\n"); }
	if (substr($sb[4],3,1) eq "1") { print(" Memory FULL !\n"); }
	if (substr($sb[5],5,1) eq "1") { print(" Unknown memory error !\n"); }
	if (substr($sb[5],7,1) eq "1") { print(" Memory is READONLY !\n"); }
# Misc and nonFatal Errors 
	if ($verbose) {
		if (substr($sb[0],4,1) eq "1") { print("Display pluged\n"); } else {print("No display plugged!\n");}
		if (substr($sb[0],5,1) eq "1") { print("Clock is not set!\n"); } else {print("Clock is set\n");}
		if (substr($sb[1],7,1) eq "1") { print("Cash Overflow ! Reduce !\n"); }
		if (substr($sb[2],2,1) eq "1") { print("Nonfiscal BON opened !\n"); }
		if (substr($sb[2],4,1) eq "1") { print("Fiscal BON opened !\n"); }
		if (substr($sb[3],1,1) eq "1") { print("Switch 2.2 is ON\n"); } else {print("Switch 2.2 is OFF\n");}
		if (substr($sb[3],2,1) eq "1") { print("Switch 2.1 is ON\n"); } else {print("Switch 2.2 is OFF\n");}
		if (substr($sb[3],3,1) eq "1") { print("Switch 1.5 is ON\n"); } else {print("Switch 1.5 is OFF\n");}
		if (substr($sb[3],4,1) eq "1") { print("Switch 1.4 is ON\n"); } else {print("Switch 1.4 is OFF\n");}
		if (substr($sb[3],5,1) eq "1") { print("Switch 1.3 is ON\n"); } else {print("Switch 1.3 is OFF\n");}
		if (substr($sb[3],6,1) eq "1") { print("Switch 1.2 is ON\n"); } else {print("Switch 1.2 is OFF\n");}
		if (substr($sb[3],7,1) eq "1") { print("Switch 1.1 is ON\n"); } else {print("Switch 1.1 is OFF\n");}
		if (substr($sb[4],4,1) eq "1") { print("Less than 50 units of memory remain !\n"); }
		if (substr($sb[4],5,1) eq "1") { print("NO FISCAL MEMORY !\n"); }
		if (substr($sb[4],7,1) eq "1") { print("Memory write error !\n"); }
		if (substr($sb[5],2,1) eq "1") { print("Memory and fiscal data set !\n"); } else { print("Memory and fiscal data are NOT set !\n"); }
		if (substr($sb[5],3,1) eq "1") { print("Tax groups set !\n"); } else { print("Tax groups are NOT set !\n"); }
		if (substr($sb[5],4,1) eq "1") { print("Printer is in fiscal mode !\n"); } else { print("Printer is NOT in fiscal mode !\n"); }
		if (substr($sb[5],6,1) eq "1") { print("Fiscal memory is cleared !\n"); }
	}
}

$#ARGV > -1 || die "\n...some arguments missing !\n\nusage :\n\nfiscalprn.pl [-v] command [data]\n\nExample:\nfiscalprn.pl -v setclock \"DD-MM-YY HH:MM:SS\"\nfiscalprn.pl -v openfiscal\nfiscalprn.pl -v add \"Apples\" \"A\" \"10.00\"\nfiscalprn.pl -v add \"Pears\" \"B\" \"10.00\"\nfiscalprn.pl -v total\nfiscalprn.pl -v closefiscal\nfiscalprn.pl -v newline\n\n";
# ok, all there, now let's interpret it !
my @cmdline = @ARGV;
if ($cmdline[0] eq "-v") {
	$verbose = 1;
	shift @cmdline;
	if ($#cmdline == -1) {die "\nWhat to verbose ?\n"};
}

my $command = shift(@cmdline);

# Initialize the fiscal printer
my $prn = new IO::Socket::INET (
                                  PeerAddr => '85.217.129.121',
                                  PeerPort => '1024',
                                  Proto => 'tcp',
                                 );

die "Could not create socket: $!\n" unless $prn;


#my $prn = Device::SerialPort->new ($port) || die "Cannot open $port !";
#$prn->baudrate(115200);
#$prn->parity("none");
#$prn->databits(8);
#$prn->stopbits(1);
#$prn->handshake("none");
#$prn->write_settings || die "Cannot setup printer !?";

my $enccmd = &encode (chr(0x20), chr(0x4a), "");
my $pass = $prn->write($enccmd) || die "Cannot communicate with Fiscal printer !";
sleep 1;
#&prn_reply($prn->input);

if ($command eq "openfiscal") {
# data format is <Operator>,<Password>,<OperatorPlace>[,<I>]
# where Operator, password & OperatorPlace are preset index
# and I means that current Bon is treated as Invoice
	$enccmd = &encode (chr(0x21), chr(0x30), "1,0000,1");
	$pass = $prn->write($enccmd) || die "Cannot communicate with Fiscal printer !";
	sleep 1;
#	&prn_reply($prn->input);
}

if ($command eq "newline") {
# data format [<count>]
	$enccmd = &encode (chr(0x21), chr(0x2c), shift(@cmdline));
	$pass = $prn->write($enccmd) || die "Cannot communicate with Fiscal printer !";
	sleep 1;
#	&prn_reply($prn->input);
}

if ($command eq "add") {
# data format [<text1>][<LF><text2>]<tab><taxgroup(in ABCD)><[sign]price>[*<qwantity>][,perc]
# text1 and text2 are both limited to 25 chars. 
	if ($#cmdline != 2) {die "\nNot enough arguments for invoice line !\n\n format is <product name><tax group (in ABCD)><price>\n"}
	$enccmd = &encode (chr(0x21), chr(0x31), shift(@cmdline).chr(0x09).shift(@cmdline).shift(@cmdline));
	$pass = $prn->write($enccmd) || die "Cannot communicate with Fiscal printer !";
	sleep 1;
#	&prn_reply($prn->input);
}

if ($command eq "total") {
# data format is [<text1>][<LF><text2>]<tab>[[Paidmode]<[sign]amount>]
	$enccmd = &encode (chr(0x21), chr(0x35), chr(0x09));
	$pass = $prn->write($enccmd) || die "Cannot communicate with Fiscal printer !";
	sleep 1;
#	&prn_reply($prn->input);
}

if ($command eq "closefiscal") {
# no data
	$enccmd = &encode (chr(0x21), chr(0x38), "");
	$pass = $prn->write($enccmd) || die "Cannot communicate with Fiscal printer !";
	sleep 1;
#	&prn_reply($prn->input);
}

if ($command eq "setclock") {
# no data
	$enccmd = &encode (chr(0x21), chr(0x3d), shift(@cmdline));
	$pass = $prn->write($enccmd) || die "Cannot communicate with Fiscal printer !";
	sleep 1;
#	&prn_reply($prn->input);
}

# Close printer handle
sleep 1;
undef $prn;

__END__
