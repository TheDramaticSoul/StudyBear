INSERT INTO USER_ENROLLMENT 
SELECT userName, teachingId, 'A'
FROM USER A, TEACHING B, CLASS C, PROFESSOR D 
WHERE A.userName = 'jbusch'
AND B.classId = C.classId
AND B.classId = 'CSCI 1301'
AND B.professorId = D.professorId
AND B.professorId = 1;

INSERT INTO USER_ENROLLMENT 
SELECT userName, teachingId, 'A'
FROM USER A, TEACHING B, CLASS C, PROFESSOR D 
WHERE A.userName = 'jbusch'
AND B.classId = C.classId
AND B.classId = 'CSCI 1302'
AND B.professorId = D.professorId
AND B.professorId = 2;

INSERT INTO USER_ENROLLMENT 
SELECT userName, teachingId, 'A'
FROM USER A, TEACHING B, CLASS C, PROFESSOR D 
WHERE A.userName = 'jbusch'
AND B.classId = C.classId
AND B.classId = 'CSCI 3400'
AND B.professorId = D.professorId
AND B.professorId = 2;

INSERT INTO USER_ENROLLMENT 
SELECT userName, teachingId, 'A'
FROM USER A, TEACHING B, CLASS C, PROFESSOR D 
WHERE A.userName = 'jbusch'
AND B.classId = C.classId
AND B.classId = 'CSCI 3520'
AND B.professorId = D.professorId
AND B.professorId = 3;


insert into user_enrollment
values('creynolds', 1, 'CSCI 1301', 'y'),
('jscott88', 1, 'CSCI 1301', 'y'),
('creynolds', 2, 'CSCI 3400', 'y'),
('odarcie', 2, 'CSCI 3400', 'y'),
('dcal', 1, 'CSCI 1301', 'y');;


insert into matchresponse(userName, otherUserName, response)
values('creynolds', 'jscott88', 'study'),
('creynolds', 'jscott88', 'study'),
('creynolds', 'dcal', 'pass'),
('creynolds', 'odarcie', 'study'),
('creynolds', 'dcal', 'study'),
('odarcie', 'dcal', 'pass'),
('odarcie', 'dcal', 'study');