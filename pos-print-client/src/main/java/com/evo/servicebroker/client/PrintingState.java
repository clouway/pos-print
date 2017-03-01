package com.evo.servicebroker.client;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
public enum PrintingState {
  QUEUE(0), PRINTING(1), PRINTED(2), ERROR(3);

  private Integer id;

  PrintingState() {
    id = 0;
  }

  PrintingState(Integer id) {
    this.id = id;
  }

  public Integer value(){
    return id;
  }

  public static PrintingState from(Integer id){
    for (PrintingState t : values()) {
      if (t.id.equals(id)) {
        return t;
      }
    }
    return null;
  }
  
}
