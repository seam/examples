INSERT INTO PREFERENCE VALUES(1,'Wiki','baseUrl',NULL,NULL,NULL,NULL,'http://www.lacewiki.org/change_me_in_database_import',0,NULL);
INSERT INTO PREFERENCE VALUES(2,'Wiki','timeZone',NULL,NULL,NULL,NULL,'CET',0,NULL);
INSERT INTO PREFERENCE VALUES(3,'Wiki','themeName',NULL,NULL,NULL,NULL,'default',0,NULL);
INSERT INTO PREFERENCE VALUES(4,'Wiki','memberArea',NULL,NULL,NULL,NULL,'Members',0,NULL);
INSERT INTO PREFERENCE VALUES(5,'Wiki','helpArea',NULL,NULL,NULL,NULL,'Help',0,NULL);
INSERT INTO PREFERENCE VALUES(6,'Wiki','defaultDocumentId',3,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(7,'Wiki','renderPermlinks',NULL,NULL,NULL,FALSE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(8,'Wiki','permlinkSuffix',NULL,NULL,NULL,NULL,'.lace',0,NULL);
INSERT INTO PREFERENCE VALUES(9,'Wiki','feedTitlePrefix',NULL,NULL,NULL,NULL,'LaceWiki - ',0,NULL);
INSERT INTO PREFERENCE VALUES(10,'Wiki','purgeFeedEntriesAfterDays',999,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(11,'Wiki','atSymbolReplacement',NULL,NULL,NULL,NULL,'(AT)',0,NULL);
INSERT INTO PREFERENCE VALUES(12,'Wiki','mainMenuLevels',3,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(13,'Wiki','mainMenuDepth',2,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(14,'Wiki','mainMenuShowAdminOnly',NULL,NULL,NULL,FALSE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(15,'Wiki','showDocumentCreatorHistory',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(16,'Wiki','showTags',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(17,'Wiki','showEmailToLoggedInOnly',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(18,'Wiki','trashArea',NULL,NULL,NULL,NULL,'Trash',0,NULL);
INSERT INTO PREFERENCE VALUES(19,'Wiki','showSiteFeedInMenu',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(20,'Wiki','workspaceSwitcherDescriptionLength',25,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(50,'DocEditor','minorRevisionEnabled',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(51,'DocEditor','regularEditAreaRows',15,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(52,'DocEditor','regularEditAreaColumns',65,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(60,'UserManagement','activationCodeSalt',NULL,NULL,NULL,NULL,'MySecretSalt123',0,NULL);
INSERT INTO PREFERENCE VALUES(61,'UserManagement','passwordRegex',NULL,NULL,NULL,NULL,'^[0-9A-Za-z]{6,15}',0,NULL);
INSERT INTO PREFERENCE VALUES(62,'UserManagement','newUserInRole',NULL,NULL,NULL,NULL,'member',0,NULL);
INSERT INTO PREFERENCE VALUES(63,'UserManagement','enableRegistration',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(64,'UserManagement','createHomeAfterUserActivation',NULL,NULL,NULL,FALSE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(65,'UserManagement','homepageDefaultContent',NULL,NULL,NULL,NULL,'This is your homepage, login to edit it.',0,NULL);

INSERT INTO PREFERENCE VALUES(70,'Comments','listAscending',NULL,NULL,NULL,FALSE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(71,'Comments','enableByDefault',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(72,'Comments','threaded',NULL,NULL,NULL,TRUE,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(110,'LastModifiedDocuments','numberOfItems',5,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(111,'LastModifiedDocuments','showUsernames',NULL,NULL,NULL,TRUE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(112,'LastModifiedDocuments','documentTitleLength',20,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(113,'LastModifiedDocuments','title',NULL,NULL,NULL,NULL,'Last Site Updates',0,NULL);

INSERT INTO PREFERENCE VALUES(120,'Blog','pageSize',5,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(121,'Blog','archiveSubscribeIcon',NULL,NULL,NULL,FALSE,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(122,'Blog','recentEntriesItems',10,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(123,'Blog','recentEntriesTruncateTitle',40,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(124,'Blog','recentEntriesSubscribeIcon',NULL,NULL,NULL,TRUE,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(130,'FeedTeasers','title',NULL,NULL,NULL,NULL,'Site news feed',0,NULL);
INSERT INTO PREFERENCE VALUES(131,'FeedTeasers','numberOfTeasers',5,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(132,'FeedTeasers','truncateDescription',200,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(133,'FeedTeasers','showAuthor',NULL,NULL,NULL,TRUE,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(140,'Flash','allowedDomains',NULL,NULL,NULL,NULL,'video.google.com,www.youtube.com',0,NULL);

INSERT INTO PREFERENCE VALUES(150,'DirMenu','title',NULL,NULL,NULL,NULL,'Directory Menu',0,NULL);
INSERT INTO PREFERENCE VALUES(151,'DirMenu','menuLevels',3,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(152,'DirMenu','menuDepth',3,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(160,'Tags','linkToCurrentDocument',NULL,NULL,NULL,TRUE,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(170,'DocPager','showNames',NULL,NULL,NULL,TRUE,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(180,'Forum','topicsPerPage',20,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(190,'JiraIssueList','title',NULL,NULL,NULL,NULL,'JIRA Issues',0,NULL);
INSERT INTO PREFERENCE VALUES(191,'JiraIssueList','truncateSummary',35,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(200,'FeedAggregator','title',NULL,NULL,NULL,NULL,'Aggregated Feeds',0,NULL);
INSERT INTO PREFERENCE VALUES(201,'FeedAggregator','truncateDescription',100,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(210,'ForumTopPosters','title',NULL,NULL,NULL,NULL,'Top Forum Posters',0,NULL);
INSERT INTO PREFERENCE VALUES(211,'ForumTopPosters','numberOfPosters',10,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(220,'FlashVideo','allowedDomains',NULL,NULL,NULL,NULL,'video.google.com,www.youtube.com',0,NULL);

INSERT INTO PREFERENCE VALUES(300,'JiraConnector','connectionTimeoutSeconds',15,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(301,'JiraConnector','replyTimeoutSeconds',10,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(302,'JiraConnector','issueListCacheUpdateTimeoutSeconds',600,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(303,'JiraConnector','issueListCacheIdleTimeoutSeconds',36000,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO PREFERENCE VALUES(310,'FeedConnector','connectionTimeoutSeconds',30,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(311,'FeedConnector','feedCacheUpdateTimeoutSeconds',600,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO PREFERENCE VALUES(312,'FeedConnector','feedCacheIdleTimeoutSeconds',36000,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO `ROLES` (`ROLE_ID`, `ACCESS_LEVEL`, `CREATED_ON`, `DISPLAY_NAME`, `NAME`, `OBJ_VERSION`) VALUES (1,1000,'2006-06-27 13:45:00','Administrator','admin',0);
INSERT INTO `ROLES` (`ROLE_ID`, `ACCESS_LEVEL`, `CREATED_ON`, `DISPLAY_NAME`, `NAME`, `OBJ_VERSION`) VALUES (2,0,'2006-06-27 13:45:00','Guest','guest',0);
INSERT INTO `ROLES` (`ROLE_ID`, `ACCESS_LEVEL`, `CREATED_ON`, `DISPLAY_NAME`, `NAME`, `OBJ_VERSION`) VALUES (3,1,'2006-06-27 13:45:00','Member','member',0);

INSERT INTO `USER_PROFILE` (`USER_PROFILE_ID`, `BIO`, `CREATED_ON`, `IMAGE_DATA`, `IMAGE_CONTENT_TYPE`, `LOCATION`, `OCCUPATION`, `SIGNATURE`, `OBJ_VERSION`, `WEBSITE`) VALUES (1,NULL,'2006-06-27 13:45:00',NULL,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO `USER_PROFILE` (`USER_PROFILE_ID`, `BIO`, `CREATED_ON`, `IMAGE_DATA`, `IMAGE_CONTENT_TYPE`, `LOCATION`, `OCCUPATION`, `SIGNATURE`, `OBJ_VERSION`, `WEBSITE`) VALUES (2,NULL,'2006-06-27 13:45:00',NULL,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO `USERS` (`USER_ID`, `ACTIVATED`, `ACTIVATION_CODE`, `CREATED_ON`, `EMAIL`, `FIRSTNAME`, `LAST_LOGIN_ON`, `LASTNAME`, `PASSWORDHASH`, `USERNAME`, `OBJ_VERSION`, `USER_PROFILE_ID`, `MEMBER_HOME_WIKI_DIRECTORY_ID`) VALUES (1,'',NULL,'2006-06-27 13:45:00','admin@email.tld','System',NULL,'Administrator','21232f297a57a5a743894a0e4a801fc3','admin',0,1,NULL);
INSERT INTO `USERS` (`USER_ID`, `ACTIVATED`, `ACTIVATION_CODE`, `CREATED_ON`, `EMAIL`, `FIRSTNAME`, `LAST_LOGIN_ON`, `LASTNAME`, `PASSWORDHASH`, `USERNAME`, `OBJ_VERSION`, `USER_PROFILE_ID`, `MEMBER_HOME_WIKI_DIRECTORY_ID`) VALUES (2,false,NULL,'2006-06-27 13:45:00','guest','Anonymous',NULL,'Guest','guest','guest',0,2,NULL);

INSERT INTO `USER_ROLE` (`USER_ID`, `ROLE_ID`) VALUES (1,1);
INSERT INTO `USER_ROLE` (`USER_ID`, `ROLE_ID`) VALUES (2,2);


INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES (1,1,'2006-09-23 13:45:00',NULL,'ROOT',0,0,'ROOT',1000,'',1,NULL,NULL,0);

INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES (2,2,'2006-09-23 13:45:00',NULL,'Start',0,0,'Start',0,false,1,NULL,1,0);

INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES(3,2,'2006-09-23 13:45:00',NULL,'Welcome!',0,1,'Welcome',0,false,1,NULL,2,0);

INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES(4,4,'2006-09-23 13:45:00',NULL,'Members',0,0,'Members',1000,'',1,NULL,1,0);

INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES (5,5,'2006-09-23 13:45:00',NULL,'Help',0,0,'Help',1000,'',1,NULL,1,0);

INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES (6,5,'2006-09-23 13:45:00',NULL,'Working with documents',0,0,'WorkingWithDocuments',1000,'',1,NULL,5,0);

INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES (7,5,'2006-09-23 13:45:00',NULL,'Wiki Text Markup',0,0,'WikiTextMarkup',1000,'',1,NULL,6,0);

INSERT INTO `WIKI_NODE` (`NODE_ID`, `AREA_NR`, `CREATED_ON`, `LAST_MODIFIED_ON`, `NAME`, `READ_ACCESS_LEVEL`, `OBJ_VERSION`, `WIKINAME`, `WRITE_ACCESS_LEVEL`, `WRITE_PROTECTED`, `CREATED_BY_USER_ID`, `LAST_MODIFIED_BY_USER_ID`, `PARENT_NODE_ID`, `RATING`) VALUES (8,8,'2006-09-23 13:45:00',NULL,'Trash',0,0,'Trash',1000,'',1,NULL,1,0);

INSERT INTO `WIKI_FILE` (`NODE_ID`, `FILE_REVISION`) VALUES (3,0);
INSERT INTO `WIKI_FILE` (`NODE_ID`, `FILE_REVISION`) VALUES (7,0);

INSERT INTO `WIKI_DIRECTORY` (`NODE_ID`, `DESCRIPTION`, `NS_LEFT`, `NS_RIGHT`, `NS_THREAD`, `DEFAULT_FILE_ID`) VALUES (1,'Root Area',1,12,1,NULL);
INSERT INTO `WIKI_DIRECTORY` (`NODE_ID`, `DESCRIPTION`, `NS_LEFT`, `NS_RIGHT`, `NS_THREAD`, `DEFAULT_FILE_ID`) VALUES (2,'Start Area',2,3,1,3);
INSERT INTO `WIKI_DIRECTORY` (`NODE_ID`, `DESCRIPTION`, `NS_LEFT`, `NS_RIGHT`, `NS_THREAD`, `DEFAULT_FILE_ID`) VALUES (4,'Default member area',8,9,1,NULL);
INSERT INTO `WIKI_DIRECTORY` (`NODE_ID`, `DESCRIPTION`, `NS_LEFT`, `NS_RIGHT`, `NS_THREAD`, `DEFAULT_FILE_ID`) VALUES (5,'Help documents',4,7,1,NULL);
INSERT INTO `WIKI_DIRECTORY` (`NODE_ID`, `DESCRIPTION`, `NS_LEFT`, `NS_RIGHT`, `NS_THREAD`, `DEFAULT_FILE_ID`) VALUES (6,'Creating and editing wiki documents',5,6,1,NULL);
INSERT INTO `WIKI_DIRECTORY` (`NODE_ID`, `DESCRIPTION`, `NS_LEFT`, `NS_RIGHT`, `NS_THREAD`, `DEFAULT_FILE_ID`) VALUES (8,'Trash Area',10,11,1,NULL);

INSERT INTO `WIKI_DOCUMENT` (`NODE_ID`, `CONTENT`, `CONTENT_MACROS`, `ENABLE_COMMENT_FORM`, `ENABLE_COMMENTS`, `ENABLE_COMMENTS_ON_FEEDS`, `FOOTER`, `FOOTER_MACROS`, `HEADER`, `HEADER_MACROS`, `NAME_AS_TITLE`) VALUES (3,'Welcome to LaceWiki.\n\nLogin with admin-admin and rebuild the search index in the administration area.',NULL,true,true,true,NULL,NULL,NULL,NULL,true);

INSERT INTO `WIKI_DOCUMENT` (`NODE_ID`, `CONTENT`, `CONTENT_MACROS`, `ENABLE_COMMENT_FORM`, `ENABLE_COMMENTS`, `ENABLE_COMMENTS_ON_FEEDS`, `FOOTER`, `FOOTER_MACROS`, `HEADER`, `HEADER_MACROS`, `NAME_AS_TITLE`) VALUES (7,'Most content on this website (blogs, blog comments, wiki pages, user profiles) is rendered using the [Seam Text=>http://docs.jboss.com/seam/latest/reference/en/html/text.html] engine. If you are creating content, it helps to know a few simple tricks.\n\n++ Basic formatting\n\nYou can emphasize words using *emphasis*, _underline_, ~strikeout~ or ^superscript^.\n\n`You can emphasize words using\n*emphasis*, _underline_, ~strikeout~ or ^superscript^.`\n\nBut if you really want to type a special character such as \\* or \\+, you need to escape it with a \\\\.\n\n`But if you really want to type a special character\nsuch as \\* or \\+, you need to escape it with a \\\\.`\n\nAlternatively, you can use special characters freely inside |monospace text|.\n\n`Alternatively, you can use special characters freely\ninside |monospace text|.`\n\n++ Block formatting\n\nOf course, you can also use \"inline quotes\".\n\n\"And block quotes.\"\n\nAnd split text across several paragraphs.\n\n`Of course, you can also use \"inline quotes\".\n\n\"And block quotes.\"\n\nAnd split text across several paragraphs.`\n\nYou can create\n\n= unorderedlists\n= of stuff\n= like this\n\nor\n\n# numbered lists\n# of other things\n\n`You can create\n= unordered lists\n= of stuff\n= like this\n\nor\n\n# numbered lists\n# of other things`\n\n++ Code Blocks\n\nA third option for embedding text that uses special characters is to use a code block, delimited by `backticks`. For example:\n\n<pre>\\`for (int i\\=0; i\\<100; i\\+\\+) {\n   log.info(\\\"Hello world!\\\");\n}\\`</pre>\n\n+ Here is a first-level heading\n\nHere is a normal paragraph.\n\n++ Here is a second-level heading\n\nAnd another paragraph.\n\n`+ Here is a first-level heading\n\nHere is a normal paragraph.\n\n++ Here is a second-level heading\n\nAnd another paragraph.`\n\n++ Links\n\nThe wiki has powerful handling for links.\n\nHTML links to [=>http://hibernate.org] or attach the link to [some link text=>http://hibernate.org].\nMy [e-mail address=>mailto:foo@bar.tld] will be automatically protected.\n\n`HTML links to [=>http://hibernate.org] or attach the\nlink to [some link text=>http://hibernate.org].\nMy [e-mail address=>mailto:foo@bar.tld] will be\nautomatically protected.`\n\nInternal wiki links simply use area and document/file names:\n\n= Link to  another document: \\[\\=\\>My Document\\] \n= Link to another document with link text: \\[A document\\=\\>My Document\\]\n= Link to another document in another area: \\[\\=\\>Another Area\\|My Document\\]\n= Link to an uploaded file or image: \\[\\=\\>My Upload\\]\n\nYou can even link to a [Hibernate JIRA issue=>hhh://2702], or a\n[Seam JIRA issue=>jbseam://1920].\n\n`You can even link to a [Hibernate JIRA issue=>hhh://2702],\nor a [Seam JIRA issue=>jbseam://1920].`\n\n++ Embedded HTML\n\nYou can even use <i>many</i> HTML tags directly, but <b>not</b> tags that would create a security vulnerability!\n\n`You can even use <i>many</i> HTML tags directly,\nbut <b>not</b> tags that would create a\nsecurity vulnerability!`',NULL,false,false,false,NULL,NULL,NULL,NULL,true);

INSERT INTO `WIKI_MENU_ITEM` (`DIRECTORY_ID`, `DISPLAY_POSITION`) VALUES (2,0);
INSERT INTO `WIKI_MENU_ITEM` (`DIRECTORY_ID`, `DISPLAY_POSITION`) VALUES (4,1);
INSERT INTO `WIKI_MENU_ITEM` (`DIRECTORY_ID`, `DISPLAY_POSITION`) VALUES (5,2);

INSERT INTO `FEED` (`FEED_ID`, `FEED_TYPE`, `FEED_LINK`, `AUTHOR`, `DESCRIPTION`, `PUBLISHED_ON`, `TITLE`, `DIRECTORY_ID`) VALUES (1,'INTERNAL','http://www.lacewiki.org/change_me_in_database_import/ROOT','System Administrator',NULL,'2006-09-23 13:45:00','ROOT',1);;

INSERT INTO `LINK_PROTOCOL` (`LINK_PROTOCOL_ID`, `LINK`, `PREFIX`, `OBJ_VERSION`) VALUES (1,'http://jira.jboss.com/jira/browse/JBSEAM-[[link]]','jbseam',0);
INSERT INTO `LINK_PROTOCOL` (`LINK_PROTOCOL_ID`, `LINK`, `PREFIX`, `OBJ_VERSION`) VALUES (2,'http://opensource.atlassian.com/projects/hibernate/browse/HHH-[[link]]','hhh',0);
INSERT INTO `LINK_PROTOCOL` (`LINK_PROTOCOL_ID`, `LINK`, `PREFIX`, `OBJ_VERSION`) VALUES (3,'http://www.youtube.com/watch?v=[[link]]','youtube',0);
