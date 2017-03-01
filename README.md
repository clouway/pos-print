## POS Print Service 

A Service for printing of receipts to cash registers or termo printers.

### Build

In pos-print
```sh
gradle clean shadowJar
```
will create build/libs/pos-print-all.jar which could be distributed 
and runned on any machine.

### Receipt Sample 

The current design requires the following JSON messages to be used for printing 
of simple receipt.

```javascript
{
    "key": "ag5jbG91d2F5dGVzdGFwcHI2CxIFT3JkZXIiHjdlZjY5MDQwNGU0N2Q1YjM2NmVmZjY1MDVhMzgyNwwLEgdSZWNlaXB0GBYM",
    "printingIp": "192.168.5.6",
    "customerDetails": {
        "address": "",
        "contractNumber": "Invoice0150061720/09/11/2007"
    },
    "transactionNumber": "7ef690404e47d5b366eff6505a3827",
    "cashierName": "John Smith",
    "printingDate": "2011-12-01 18:00:00",
    "receiptType": 1,
    "receiptItems": [
        {
            "name": "Invoice0150061720/09/11/2007",
            "quantity": 1,
            "price": 0.01
        }
    ],
    "amount": 0.01,
    "department": "01"
}
```

### Supported Models 
 * Datecs FP705
