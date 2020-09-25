
-- Employee --
DROP TABLE FINAL_EMPLOYEE CASCADE CONSTRAINTS;
CREATE TABLE FINAL_EMPLOYEE (
				  EMPLOYEE_ID        NUMBER(6) GENERATED AS IDENTITY PRIMARY KEY ,
				  USERNAME           VARCHAR2(100) NOT NULL,
				  PASSWORD          VARCHAR2(2000) NOT NULL,
                  EMPLOYEE_TYPE     CHAR(1 BYTE) DEFAULT 'E',
                  DELETED              CHAR(1 BYTE) DEFAULT '0',
                  CREATED_ON        TIMESTAMP(3),
                  LAST_MODIFIED     TIMESTAMP(3)
			);
            
            
-- File --
DROP TABLE FINAL_FILE CASCADE CONSTRAINTS;
CREATE TABLE FINAL_FILE (
				  FILE_ID                 NUMBER(6) GENERATED AS IDENTITY PRIMARY KEY ,
				  NAME                 VARCHAR2(100) NOT NULL,
				  URL                    VARCHAR2(2000) NOT NULL,
                  EMPLOYEE_ID         NUMBER(6) NOT NULL,
                  DELETED               CHAR(1 BYTE) DEFAULT '0',
                  CREATED_ON         TIMESTAMP(3),
                  LAST_MODIFIED      TIMESTAMP(3),
                  FOREIGN KEY ("EMPLOYEE_ID") REFERENCES "FINAL_EMPLOYEE" ("EMPLOYEE_ID")
			);            

-- Share File --
DROP TABLE FINAL_SHARE_FILE CASCADE CONSTRAINTS;
CREATE TABLE FINAL_SHARE_FILE (
				  SHARE_FILE_ID                  NUMBER(6) GENERATED AS IDENTITY PRIMARY KEY ,
				  FILE_ID                            NUMBER(6) NOT NULL,
				  SHARING_EMPLOYEE_ID       NUMBER(6) NOT NULL,
                  DELETED                          CHAR(1 BYTE) DEFAULT '0',
                  CREATED_ON                    TIMESTAMP(3),
                  LAST_MODIFIED                 TIMESTAMP(3),
                  FOREIGN KEY ("SHARING_EMPLOYEE_ID") REFERENCES "FINAL_EMPLOYEE" ("EMPLOYEE_ID"),
                  FOREIGN KEY ("FILE_ID") REFERENCES "FINAL_FILE" ("FILE_ID")
                  ); 
                  
-- Generate Link --
DROP TABLE FINAL_GENERATE_LINK CASCADE CONSTRAINTS;
CREATE TABLE FINAL_GENERATE_LINK (
				  GENERATE_LINK_ID              NUMBER(6) GENERATED AS IDENTITY PRIMARY KEY ,
				  FILE_ID                       NUMBER(6) NOT NULL,
                  LINK                          VARCHAR2(20) NOT NULL,
                  EXPIRE_TIME                   TIMESTAMP(3),
                  DELETED                       CHAR(1 BYTE) DEFAULT '0',
                  CREATED_ON                    TIMESTAMP(3),
                  LAST_MODIFIED                 TIMESTAMP(3),
                  FOREIGN KEY ("FILE_ID") REFERENCES "FINAL_FILE" ("FILE_ID")
                  );                   

COMMIT;         
