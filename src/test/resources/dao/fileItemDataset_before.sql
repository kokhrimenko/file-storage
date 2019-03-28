insert into USERS (id, username, password) VALUES
	(1, 'TC-user1', 'TC-pass1'),
	(2, 'TC-user2', 'TC-pass2'),
	(3, 'TC-user3', 'TC-pass3'),
	(4, 'TC-user4', 'TC-pass4'),
	(5, 'TC-user5', 'TC-pass5');

insert into FILE_ITEM (id, name, fs_path, user_id) VALUES
	(1, 'TC-fileName1', '/tmp/1/TC-fileName1.txt', 1),
	(2, 'TC-fileName2', '/tmp/1/TC-fileName2.txt', 1),
	(3, 'TC-fileName3', '/tmp/1/TC-fileName3.txt', 1),
	
	(11, 'TC-fileName11', '/tmp/2/TC-fileName11.txt', 2),
	
	(31, 'TC-fileName31', '/tmp/4/TC-fileName31.txt', 4),
	(32, 'TC-fileName32', '/tmp/4/TC-fileName32.txt', 4),
	(33, 'TC-fileName33', '/tmp/4/TC-fileName33.txt', 4),
	(34, 'TC-fileName34', '/tmp/4/TC-fileName34.txt', 4),
	(35, 'TC-fileName35', '/tmp/4/TC-fileName35.txt', 4),
	
	(41, 'TC-fileName41', '/tmp/5/TC-fileName41.txt', 5),
	(42, 'TC-fileName42', '/tmp/5/TC-fileName142.txt', 5);

insert into USER_SHARE (USER_ID, FILE_ITEM_ID, created_on) VALUES
	(2, 2, null),
	(2, 3, null);