SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS UNIVERSITY;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS USER_BLOCKED;
DROP TABLE IF EXISTS MESSAGES;
DROP TABLE IF EXISTS CLASS;
DROP TABLE IF EXISTS PROFESSOR;
DROP TABLE IF EXISTS TEACHING;
DROP TABLE IF EXISTS USER_ENROLLMENT;
DROP TABLE IF EXISTS USER_ACTIVATION;
DROP TABLE IF EXISTS MatchResponse;


CREATE TABLE UNIVERSITY ( 
universityName VARCHAR(80) PRIMARY KEY
)ENGINE=INNODB;

CREATE TABLE USER (userName VARCHAR(20) PRIMARY KEY,
firstName VARCHAR(20),
lastName VARCHAR(20),
password VARCHAR(60),
email VARCHAR(30),
biography VARCHAR(200),
universityName VARCHAR(80), 
accountStatus VARCHAR(1),
FOREIGN KEY (universityName) REFERENCES UNIVERSITY(universityName)
)ENGINE=INNODB;

CREATE TABLE USER_BLOCKED (
userName VARCHAR(20),
blockeduserName VARCHAR(20),
FOREIGN KEY (userName) REFERENCES USER(userName),
FOREIGN KEY (blockeduserName) REFERENCES USER(userName)
)ENGINE=INNODB;

CREATE TABLE MESSAGES(
msgId INTEGER(10) AUTO_INCREMENT PRIMARY KEY,
sendingUser VARCHAR(20), 
receivingUser VARCHAR(20), 
body VARCHAR(250),
subject VARCHAR(100),
dateTime DATETIME,
FOREIGN KEY (sendingUser) REFERENCES USER(userName),
FOREIGN KEY (receivingUser) REFERENCES USER(userName)
)ENGINE=INNODB;

CREATE TABLE CLASS (
classId VARCHAR(10),
universityName VARCHAR(80), 
className VARCHAR(50),
major VARCHAR(50),
PRIMARY KEY(classId, universityName),
FOREIGN KEY (universityName) REFERENCES UNIVERSITY(universityName)
)ENGINE=INNODB;

CREATE TABLE PROFESSOR(
professorId INTEGER(10) AUTO_INCREMENT UNIQUE KEY,
professorFname VARCHAR(20),
professorLname VARCHAR(20),
PRIMARY KEY(professorFname, professorLname)
)ENGINE = INNODB;

CREATE TABLE TEACHING(
teachingId INTEGER(10) NOT NULL AUTO_INCREMENT UNIQUE KEY,
professorId INTEGER(10),
classId VARCHAR(10),
PRIMARY KEY (professorId, classId),
FOREIGN KEY(professorId) REFERENCES PROFESSOR(professorId),
FOREIGN KEY(classId) REFERENCES CLASS(classId)
)ENGINE = INNODB;

CREATE TABLE USER_ENROLLMENT(
userName VARCHAR(20),
professorId INTEGER(10),
classId VARCHAR(10),
enrollmentStatus VARCHAR(1),
PRIMARY KEY (userName, professorId, classId),
FOREIGN KEY (userName) REFERENCES USER(userName),
FOREIGN KEY (professorId) REFERENCES TEACHING(professorId),
FOREIGN KEY (classId) REFERENCES TEACHING(classId)
)ENGINE=INNODB;

CREATE TABLE USER_ACTIVATION(
userName VARCHAR(20),
actId VARCHAR(30),
FOREIGN KEY(userName) REFERENCES USER(userName)
)ENGINE=INNODB;

CREATE TABLE MatchResponse(
responseId INTEGER NOT NULL AUTO_INCREMENT,
userName VARCHAR(20),
otherUserName VARCHAR(20),
response VARCHAR(20),
PRIMARY KEY (responseId),
FOREIGN KEY (userName) REFERENCES USER(userName),
FOREIGN KEY (otherUserName) REFERENCES USER(userName)
)ENGINE=INNODB;

INSERT INTO UNIVERSITY VALUES 
('Georgia Regents University'),
('University of South Carolina - Aiken');

INSERT INTO CLASS VALUES 
('CSCI 2700','Georgia Regents University','Ethics in Computer Science','Computer Science'),
('CSCI 3030','Georgia Regents University','Math Structures for CS','Computer Science'),
('CSCI 3271','Georgia Regents University','Operating Systems I','Computer Science'),
('CSCI 3300','Georgia Regents University','Programming Languages','Computer Science'),
('CSCI 3370','Georgia Regents University','Assembly Lang Programming','Computer Science'),
('CSCI 3500','Georgia Regents University','Theory of Computation','Computer Science'),
('CSCI 3520','Georgia Regents University','Introduction to Cyber Security','Computer Science'),
('CSCI 3531','Georgia Regents University','Intro to Defensive Cyber Operations','Computer Science'),
('CSCI 3532','Georgia Regents University','Cyb Network Def & Counter Meas','Computer Science'),
('CSCI 3600','Georgia Regents University','Internet Programming','Computer Science'),
('CSCI 4711','Georgia Regents University','Software Engineering','Computer Science'),
('MATH 1111','Georgia Regents University','College Algebra','Mathematics'),
('MATH 1113','Georgia Regents University','Precalculus Mathematics','Mathematics'),
('MATH 2011','Georgia Regents University','Calculus & Analytical Geometry I','Mathematics'),
('MATH 2012','Georgia Regents University','Calculus & Analytical Geometry II','Mathematics'),
('MATH 2013','Georgia Regents University','Calculus & Analytical Geometry III','Mathematics'),
('MATH 2030','Georgia Regents University','Logic & Set Theory','Mathematics'),
('MATH 2210','Georgia Regents University','Elementary Statistics','Mathematics'),
('MATH 3020','Georgia Regents University','Differential Equations','Mathematics'),
('MATH 3280','Georgia Regents University','Linear Algebra','Mathematics'),
('MATH 4011','Georgia Regents University','Real Variables I','Mathematics'),
('MATH 4211','Georgia Regents University','Modern Abstract Algebra I','Mathematics'),
('CSCI A101','University of South Carolina - Aiken','Introduction to Computer Concepts','Computer Science'),
('CSCI A102','University of South Carolina - Aiken','Computer Apps & Programming','Computer Science'),
('CSCI A145','University of South Carolina - Aiken','Introduction to Algorithmic Design I','Computer Science'),
('CSCI A146','University of South Carolina - Aiken','Introduction to Algorithmic Design II','Computer Science'),
('CSCI A210','University of South Carolina - Aiken','Computer Organization & Assembly','Computer Science'),
('CSCI A220','University of South Carolina - Aiken','Data Structures and Algorithms','Computer Science'),
('CSCI A240','University of South Carolina - Aiken','Intro to Software Engineering','Computer Science'),
('CSCI A320','University of South Carolina - Aiken','Practical Java Programming','Computer Science'),
('CSCI A340','University of South Carolina - Aiken','Mobile Computing','Computer Science'),
('CSCI A350','University of South Carolina - Aiken','Computer Graphics','Computer Science'),
('CSCI A399','University of South Carolina - Aiken','Network Programming','Computer Science'),
('CSCI A492','University of South Carolina - Aiken','Image Processing','Computer Science'),
('CSCI A520','University of South Carolina - Aiken','Database System Design','Computer Science'),
('MATH A108','University of South Carolina - Aiken','Applied College Algebra','Mathematics'),
('MATH A111','University of South Carolina - Aiken','Precalculus Mathematics I','Mathematics'),
('MATH A112','University of South Carolina - Aiken','Precalculus Mathematics II','Mathematics'),
('MATH A122','University of South Carolina - Aiken','Survey of Calculus with Applications','Mathematics'),
('MATH A141','University of South Carolina - Aiken','Calculus I','Mathematics'),
('MATH A142','University of South Carolina - Aiken','Calculus II','Mathematics'),
('MATH A174','University of South Carolina - Aiken','Discrete Math for Computer Science','Mathematics'),
('MATH A241','University of South Carolina - Aiken','Calculus III','Mathematics'),
('MATH A242','University of South Carolina - Aiken','Calculus IV','Mathematics'),
('MATH A554','University of South Carolina - Aiken','Introduction to Analysis','Mathematics');

INSERT INTO PROFESSOR VALUES
(1,'Michael','Dowell'),
(2,'Onyeka','Ezenwoye'),
(3,'Joanne','Sexton'),
(4,'Harley','Eades'),
(5,'Paul','York'),
(6,'Ryan','Wilson'),
(7,'Ron','Martin'),
(8,'Nicki','Reich'),
(9,'Wafa','Abed'),
(10,'Laurentiu','Sega'),
(11,'Sam','Robinson'),
(12,'John','Sligar'),
(13,'Cornelius','Stallman'),
(14,'Robert','Scott'),
(15,'Eric','Numfor'),
(16,'Christopher','Terry'),
(17,'Hani','Abusalem'),
(18,'Rao','Li'),
(19,'Zhenheng','Li'),
(20,'Yilian','Zhang'),
(21,'Tieling','Chen'),
(22,'David','Jaspers'),
(23,'Cynthia','Gonzalez'),
(24,'Richard','Terlizzi'),
(25,'Thomas','Reid'),
(26,'Koffi','Fadimba'),
(27,'Reginald','Koo'),
(28,'Mohammad','Hailat');

INSERT INTO TEACHING VALUES 
(1,1,'CSCI 3370'),
(2,2,'CSCI 4711'),
(3,3,'CSCI 2700'),
(4,3,'CSCI 3271'),
(5,4,'CSCI 3300'),
(6,4,'CSCI 3500'),
(7,5,'CSCI 3600'),
(8,6,'CSCI 3520'),
(9,7,'CSCI 3531'),
(10,7,'CSCI 3532');

INSERT INTO USER VALUES 
('creynolds','Chad','Reynolds','$2y$10$tXV0VFMPwOxAp0nMx.RlKe85jbvJRjPIDRKY4CUBL.Yf8QbG8Zuba','creyno17@gru.edu','I write code','Georgia Regents University','A'),
('jscott88','Jeremy','Scott','$2y$10$IA9WirbUDe/lNAsDqtNCXeWt4rgoG4VZPuQCAKyqMKNc5F0KPy802','jeremy.scott88@gmail.com','Cheeeese','Georgia Regents University','A'),
('dcal','Daz','Callaham','$2y$10$IA9WirbUDe/lNAsDqtNCXeWt4rgoG4VZPuQCAKyqMKNc5F0KPy802','@.edu','Lets study','Georgia Regents University','A'),
('someguy','some','guy','$2y$10$IA9WirbUDe/lNAsDqtNCXeWt4rgoG4VZPuQCAKyqMKNc5F0KPy802','@.edu','Yo','Georgia Regents University','A'),
('somegal','some','gal','$2y$10$IA9WirbUDe/lNAsDqtNCXeWt4rgoG4VZPuQCAKyqMKNc5F0KPy802','@.edu','Yo','Georgia Regents University','A'),
('odarcie','Darcie','Odom','$2y$10$nw6KWoHehoUlQ/b1AnlMiuMWbfvcfIbB/TaLFaGLCsTf42wXfTdTW','darcieo@gru.edu','Hi!','Georgia Regents University','A');

insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'jbeezy88', 'hey! we have some classes together. Wanna study sometime?', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values ('jbeezy88', 'odarcie', 'sure, when can you meet up?', 'hi', now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values ('odarcie', 'jbeezy88', 'does 6pm tomorrow at the school work for you?', 'hi', now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('jbeezy88', 'odarcie', 'yeah, lets meet at allgood and find a study room', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'jbeezy88', 'perfect! see you tomorrow!', 'hi' , now());

insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'jscott', 'yo!', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values ('jscott', 'odarcie', 'howdy!', 'hi', now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values ('odarcie', 'jscott', 'this is cheese this is cheese this is cheese this is cheese', 'hi', now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('jscott', 'odarcie', 'cheeeeeeeeeeese', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'jscott', ':)', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values ('jscott', 'odarcie', 'so much cheese', 'hi', now());

insert into messages (sendingUser, receivingUser, body, subject, dateTime) values ('creynolds', 'odarcie', 'how do you count cows? ', 'hi', now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'creynolds', 'how?', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values ('creynolds', 'odarcie', 'with a COWculator!', 'hi', now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('creynolds', 'odarcie', ':D', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie','creynolds', 'That was worse than a Dowell joke', 'hi' , now());

insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'dcal', 'how do astronomers organize a party?', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'dcal', 'they planet!', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('dcal', 'odarcie', 'really Darcie?', 'hi' , now());
insert into messages (sendingUser, receivingUser, body, subject, dateTime) values('odarcie', 'dcal', 'that was a good one!', 'hi' , now());

insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('odarcie', 2, 'CSCI 4711', 'A');
insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('odarcie', 4, 'CSCI 3500', 'A');
insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('creynolds', 2, 'CSCI 4711', 'A');
insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('creynolds', 4, 'CSCI 3500', 'A');
insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('jscott88', 1, 'CSCI 3370', 'A');
insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('jscott88', 3, 'CSCI 2700', 'A');
insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('dcal', 2, 'CSCI 4711', 'A');
insert into USER_ENROLLMENT (userName, professorId, classId, enrollmentStatus) values('dcal', 3, 'CSCI 2700', 'A');

SET FOREIGN_KEY_CHECKS = 1;
