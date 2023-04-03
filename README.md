# Getting Started
This project aims to show how to use Debezium as database monitor to help performing tasks

## REFERENCES

[Debezium](https://debezium.io/) is an open source distributed platform for __Change Data Capture__ (CDC).
[MySQL](https://www.mysql.com/) database used in this example

<br>

## OVERVIEW
In this project is showed how to implement Debezium programmatically and monitor the tables customer and products. The table customer has <span style="color:red">__ALL__</span> fields are monitored, products <span style="color:red">__ONLY__</span> when his id and price.

Debezium has connectors for many databases as MySQL, PostgreSQL, SQL Server, Oracle, etc. (see all conectors here)[https://debezium.io/documentation/reference/stable/connectors/index.html]

<br>

## ATENTTION
Each database has your on rules to enable/use CDC, check for your database. For MySQL:

| Property | Description                                                                                                                                                                                            |
| :------- |:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| server-id | __The value for the server-id must be unique__ for each server and replication client in the MySQL cluster. During MySQL connector set up, Debezium assigns a unique server ID to the connector.       |
| log_bin | The value of log_bin is the base name of the sequence of binlog files.                                                                                                                                 |
| binlog_format | The binlog-format must be set to ROW or row.                                                                                                                                                           |
| binlog_row_image | The binlog_row_image must be set to FULL or full.                                                                                                                                                      |
| expire_logs_days | This is the number of days for automatic binlog file removal. The default is 0, which means no automatic removal. Set the value to match the needs of your environment. See MySQL purges binlog files. |

For more MySQL details check here [link](https://debezium.io/documentation/reference/stable/connectors/mysql.html#:~:text=Descriptions%20of%20MySQL%20binlog%20configuration%20properties).

## TESTING

Before start add bellow line into your hosts file
```shell
127.0.0.1	kafka
```

1. Start your servers instances with Docker. Open the terminal / command prompt, go to the application folder and type:
```shell
docker-compose up -d
```
2. Connect to MySQL docker instance:
```shell
docker exec -it mysql-server bash
```

3. Once inside, login into MySQL server:
```shell
mysql --user=root --password=root
```

4. With your IDE or using another terminal, run the application:
```shell
./mvnw spring-boot:run
```

5. Return to mysql terminal and select the database:
```shell
USE storeDB;
```

6. And, run INSERT:
```shell
INSERT INTO storeDB.customer (first_name, last_name, email) VALUES ('John','Doe','john.doe@acme.com');
```

7. Look in the IDE console or in the other terminal, you will see a log like:
```log
2023-03-31 13:37:02.246  INFO 21312 --- [pool-1-thread-1] i.d.connector.common.BaseSourceTask      : 9 records sent during previous 00:02:51.653, last recorded offset: {transaction_id=null, ts_sec=1680280622, file=binlog.000002, pos=4440, row=1, server_id=1, event=3}
2023-03-31 13:37:02.246 DEBUG 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : Key = Struct{id=1}, Value = Struct{after=Struct{id=1,email=john.doe@acme.com,first_name=John,last_name=Doe},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280622000,db=storeDB,table=customer,server_id=1,file=binlog.000002,pos=4778,row=0,thread=11},op=c,ts_ms=1680280622099}
2023-03-31 13:37:02.246 DEBUG 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : SourceRecordChangeValue = 'Struct{after=Struct{id=1,email=john.doe@acme.com,first_name=John,last_name=Doe},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280622000,db=storeDB,table=customer,server_id=1,file=binlog.000002,pos=4778,row=0,thread=11},op=c,ts_ms=1680280622099}'
2023-03-31 13:37:02.248 DEBUG 21312 --- [pool-1-thread-1] br.com.williamrocha.utils.DebeziumUtils  : Key = Struct{id=1}, Value = Struct{after=Struct{id=1,email=john.doe@acme.com,first_name=John,last_name=Doe},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280622000,db=storeDB,table=customer,server_id=1,file=binlog.000002,pos=4778,row=0,thread=11},op=c,ts_ms=1680280622099}
2023-03-31 13:37:02.248 DEBUG 21312 --- [pool-1-thread-1] br.com.williamrocha.utils.DebeziumUtils  : Key = Struct{id=1}, Value = Struct{after=Struct{id=1,email=john.doe@acme.com,first_name=John,last_name=Doe},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280622000,db=storeDB,table=customer,server_id=1,file=binlog.000002,pos=4778,row=0,thread=11},op=c,ts_ms=1680280622099}
2023-03-31 13:37:02.253  INFO 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : Updated Data Operation:CREATE Before: null After: {last_name=Doe, id=1, first_name=John, email=john.doe@acme.com}
2023-03-31 13:37:02.267  INFO 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : Updated Data JSON Operation:CREATE Before: null After: {"last_name":"Doe","id":1,"first_name":"John","email":"john.doe@acme.com"}
```

8. And, run INSERT:
```shell
INSERT INTO storeDB.product (description, price) VALUES ('Product x',1.23);
```

9. Look in the IDE console or in the other terminal, you will see a log like:
```shell
2023-03-31 13:37:58.389 DEBUG 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : Key = Struct{id=1}, Value = Struct{after=Struct{id=1,price=1.23},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280678000,db=storeDB,table=product,server_id=1,file=binlog.000002,pos=5269,row=0,thread=11},op=c,ts_ms=1680280678325}
2023-03-31 13:37:58.390 DEBUG 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : SourceRecordChangeValue = 'Struct{after=Struct{id=1,price=1.23},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280678000,db=storeDB,table=product,server_id=1,file=binlog.000002,pos=5269,row=0,thread=11},op=c,ts_ms=1680280678325}'
2023-03-31 13:37:58.390 DEBUG 21312 --- [pool-1-thread-1] br.com.williamrocha.utils.DebeziumUtils  : Key = Struct{id=1}, Value = Struct{after=Struct{id=1,price=1.23},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280678000,db=storeDB,table=product,server_id=1,file=binlog.000002,pos=5269,row=0,thread=11},op=c,ts_ms=1680280678325}
2023-03-31 13:37:58.390 DEBUG 21312 --- [pool-1-thread-1] br.com.williamrocha.utils.DebeziumUtils  : Key = Struct{id=1}, Value = Struct{after=Struct{id=1,price=1.23},source=Struct{version=1.9.7.Final,connector=mysql,name=store-mysql-db-server,ts_ms=1680280678000,db=storeDB,table=product,server_id=1,file=binlog.000002,pos=5269,row=0,thread=11},op=c,ts_ms=1680280678325}
2023-03-31 13:37:58.390  INFO 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : Updated Data Operation:CREATE Before: null After: {price=1.23, id=1}
2023-03-31 13:37:58.440  INFO 21312 --- [pool-1-thread-1] b.c.w.listener.DebeziumListener          : Updated Data JSON Operation:CREATE Before: null After: {"price":1.23,"id":1}
```

