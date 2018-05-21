## POS Print Service 

A Service for printing of receipts to cash registers or termo printers.


### Build Status

[![Build Status](https://travis-ci.org/clouway/pos-print.svg)](https://travis-ci.org/clouway/pos-print)

### Build

In pos-print
```sh
gradle clean shadowJar
```
will create build/libs/pos-print-all.jar which could be distributed 
and runned on any machine.

### Using from Docker

```sh
docker run -it clouway/posprint:1.1 --dbHost localhost:27017 --dbName posprint
```

### Receipt Sample 

The current design requires the following JSON messages to be used for printing 
of simple receipt.

```javascript
{
  "properties": {
    "sourceIp": "89.100.10.5",
    "operatorId": "1",
    "fiscal": true,
    "receipt": {
      "properties": {
        "receiptId": "123",
        "currency": "USD",
        "prefixLines": [
          "Customer: John"
        ],
        "suffixLines": [
          
        ],
        "amount": 1.0,
        "receiptItems": [
            {
            "properties": {
              "name": {
                  "type": "string",
                  "description": "Name of the item",
                  "example": null
                },
                "quantity": 2.0,
                "price": 1.0
              }
            }
          ]
        }
      }
    }
  }
}
```

### Supported Models 
 * Datecs FP705
