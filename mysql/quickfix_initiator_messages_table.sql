USE `fixapisampledb`;

DROP TABLE IF EXISTS quickfix_initiator_messages;

CREATE TABLE quickfix_initiator_messages (
  beginstring CHAR(8) NOT NULL,
  sendercompid VARCHAR(64) NOT NULL,
  sendersubid VARCHAR(64) NOT NULL,
  senderlocid VARCHAR(64) NOT NULL,
  targetcompid VARCHAR(64) NOT NULL,
  targetsubid VARCHAR(64) NOT NULL,
  targetlocid VARCHAR(64) NOT NULL,
  session_qualifier VARCHAR(64) NOT NULL,
  msgseqnum INT NOT NULL, 
  message TEXT NOT NULL,
  PRIMARY KEY (beginstring, sendercompid, sendersubid, senderlocid, 
  				targetcompid, targetsubid, targetlocid, session_qualifier,
  				msgseqnum)
);