USE `fixapisampledb`;

DROP TABLE IF EXISTS quickfix_initiator_messages_log_outgoing;

CREATE TABLE quickfix_initiator_messages_log_outgoing (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  time DATETIME NOT NULL,
  beginstring CHAR(8) NOT NULL,
  sendercompid VARCHAR(64) NOT NULL,
  sendersubid VARCHAR(64) NOT NULL,
  senderlocid VARCHAR(64) NOT NULL,
  targetcompid VARCHAR(64) NOT NULL,
  targetsubid VARCHAR(64) NOT NULL,
  targetlocid VARCHAR(64) NOT NULL,
  session_qualifier VARCHAR(64) NOT NULL,
  text TEXT NOT NULL,
  PRIMARY KEY (id)
);