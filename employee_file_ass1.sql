
-- Employee --
DROP TABLE AS1_EMPLOYEE CASCADE CONSTRAINTS;
CREATE TABLE AS1_EMPLOYEE (
				  EMPLOYEE_ID        NUMBER(6) GENERATED AS IDENTITY PRIMARY KEY ,
				  USERNAME           VARCHAR2(100) NOT NULL,
				  PASSWORD          VARCHAR2(2000) NOT NULL,
                  EMPLOYEE_TYPE     CHAR(1 BYTE) DEFAULT 'E',
                  DELETED              CHAR(1 BYTE) DEFAULT '0',
                  CREATED_ON        TIMESTAMP(3),
                  LAST_MODIFIED     TIMESTAMP(3)
			);
            
            
-- File --
DROP TABLE AS1_FILE CASCADE CONSTRAINTS;
CREATE TABLE AS1_FILE (
				  FILE_ID                 NUMBER(6) GENERATED AS IDENTITY PRIMARY KEY ,
				  NAME                 VARCHAR2(100) NOT NULL,
				  URL                    VARCHAR2(2000) NOT NULL,
                  EMPLOYEE_ID         NUMBER(6) NOT NULL,
                  DELETED               CHAR(1 BYTE) DEFAULT '0',
                  CREATED_ON         TIMESTAMP(3),
                  LAST_MODIFIED      TIMESTAMP(3),
                  FOREIGN KEY ("EMPLOYEE_ID") REFERENCES "AS1_EMPLOYEE" ("EMPLOYEE_ID")
			);            

-- Share File --
DROP TABLE AS1_SHARE_FILE CASCADE CONSTRAINTS;
CREATE TABLE AS1_SHARE_FILE (
				  SHARE_FILE_ID                  NUMBER(6) GENERATED AS IDENTITY PRIMARY KEY ,
				  FILE_ID                            NUMBER(6) NOT NULL,
				  SHARING_EMPLOYEE_ID       NUMBER(6) NOT NULL,
                  DELETED                          CHAR(1 BYTE) DEFAULT '0',
                  CREATED_ON                    TIMESTAMP(3),
                  LAST_MODIFIED                 TIMESTAMP(3),
                  FOREIGN KEY ("SHARING_EMPLOYEE_ID") REFERENCES "AS1_EMPLOYEE" ("EMPLOYEE_ID"),
                  FOREIGN KEY ("FILE_ID") REFERENCES "AS1_FILE" ("FILE_ID")
                  ); 

COMMIT;         