package com.clouway.pos.print.printer;

import com.google.inject.Module;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public interface PrintingModuleBuilder {
  
  Module buildModule();

}
