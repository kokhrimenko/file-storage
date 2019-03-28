insert into USERS (id, username, password) VALUES
	(1, 'TC-user1', 'TC-pass1'),
	(2, 'TC-user2', 'TC-pass2'),
	(3, 'TC-user3', 'TC-pass3'),;

insert into FILE_ITEM (id, name, fs_path, user_id) VALUES
	(1, 'TC-fileName1', '/tmp/1/TC-fileName1.txt', 1),
	(2, 'TC-fileName2', '/tmp/1/TC-fileName2.txt', 1);

insert into USER_SHARE (USER_ID, FILE_ITEM_ID, created_on) VALUES
	(2, 2, null),
	(3, 2, null);