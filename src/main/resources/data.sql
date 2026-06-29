-- ====================================================================
-- DUMMY DATA
-- 기준일: 2025-03-03
-- users(5) · books(36) · reviews(15) · review_likes(15)
-- comments(13) · notifications(10) · dashboard_batch_execution(5)
-- 모든 테스트 유저 비밀번호: password123  (BCrypt $2a$10$)
-- UUID v7 (시간 기반 정렬 가능 UUID)
-- ====================================================================

-- ============================
-- USERS (5건)
-- ============================
INSERT INTO users
(id, email, nickname, password, created_at, updated_at, deleted_at)
VALUES
    ('019435e8-3d00-7a3b-8199-c6b41c80317f',
     'minjun.kim@example.com', '김민준',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '2025-01-05 10:00:00+00', '2025-01-05 10:00:00+00', NULL);

INSERT INTO users
(id, email, nickname, password, created_at, updated_at, deleted_at)
VALUES
    ('01944652-8240-7bdd-8fac-4ee446685257',
     'seoyeon.lee@example.com', '이서연',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '2025-01-08 14:30:00+00', '2025-01-08 14:30:00+00', NULL);

INSERT INTO users
(id, email, nickname, password, created_at, updated_at, deleted_at)
VALUES
    ('019459cb-8e20-7392-af22-582a23b8c1e9',
     'dohyun.park@example.com', '박도현',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '2025-01-12 09:15:00+00', '2025-01-12 09:15:00+00', NULL);

INSERT INTO users
(id, email, nickname, password, created_at, updated_at, deleted_at)
VALUES
    ('0194c266-bae0-71a3-af67-19acad3c2d6d',
     'yujin.choi@example.com', '최유진',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '2025-02-01 16:45:00+00', '2025-02-01 16:45:00+00', NULL);

INSERT INTO users
(id, email, nickname, password, created_at, updated_at, deleted_at)
VALUES
    ('0195042f-db00-7e46-8590-67e08b9d2434',
     'haeun.jung@example.com', '정하은',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '2025-02-14 11:20:00+00', '2025-02-14 11:20:00+00', NULL);

-- ============================
-- BOOKS (36건 = 기존 6 + 추가 30)
-- ============================
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('0194263e-3a80-7972-8208-ba3c6c031199',
     '9788936434120', '채식주의자', '한강',
     '2016년 맨부커 인터내셔널상 수상작. 어느 날 갑자기 채식주의자가 된 아내와 그녀를 둘러싼 가족들의 이야기를 담은 소설.',
     '창비', '2007-10-30',
     'https://image.aladin.co.kr/product/1/22/cover/8936434128_1.jpg',
     3, 13, 4.3333333333,
     '2025-01-02 09:00:00+00', '2025-03-02 01:00:00+00', NULL);

INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('0194263f-24e0-707a-8dfe-2a2217fc695a',
     '9788936434267', '82년생 김지영', '조남주',
     '1982년생 여성 김지영의 삶을 통해 한국 사회 여성이 겪는 차별과 억압을 사실적으로 그린 소설.',
     '민음사', '2016-10-14',
     'https://image.aladin.co.kr/product/9312/28/cover/8936434268_1.jpg',
     3, 12, 4.0,
     '2025-01-02 09:01:00+00', '2025-03-02 01:00:00+00', NULL);

INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942640-0f40-73b8-a687-7991815ef6d1',
     '9788954641371', '아몬드', '손원평',
     '선천적으로 감정을 느끼지 못하는 소년 윤재가 세상과 소통하며 성장하는 이야기를 담은 청소년 소설.',
     '창비', '2017-03-02',
     'https://image.aladin.co.kr/product/10894/15/cover/8954641377_1.jpg',
     3, 14, 4.6666666667,
     '2025-01-02 09:02:00+00', '2025-03-02 01:00:00+00', NULL);

INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942640-f9a0-706c-8cb9-c18a8fadc1a6',
     '9788937473890', '완전한 행복', '정유정',
     '완벽한 행복을 위해 모든 것을 통제하려는 한 여성의 이야기를 담은 심리 스릴러 소설.',
     '은행나무', '2021-08-16',
     'https://image.aladin.co.kr/product/27695/7/cover/8937473895_1.jpg',
     3, 11, 3.6666666667,
     '2025-01-02 09:03:00+00', '2025-03-02 01:00:00+00', NULL);

INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942641-e400-7b74-ace2-8223a65ed389',
     '9791165341909', '불편한 편의점', '김호연',
     '서울역 앞 편의점을 배경으로 다양한 사람들이 서로의 상처를 치유해가는 따뜻한 이야기.',
     '나무옆의자', '2021-04-20',
     'https://image.aladin.co.kr/product/26979/53/cover/k952731154_1.jpg',
     2, 9, 4.5,
     '2025-01-02 09:04:00+00', '2025-03-02 01:00:00+00', NULL);

INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942642-ce60-78b8-8e1b-b2f86b65a6a4',
     '9791165341725', '달러구트 꿈 백화점', '이미예',
     '사람들의 꿈을 사고파는 신비로운 백화점을 배경으로 펼쳐지는 힐링 판타지 소설.',
     '팩토리나인', '2020-07-08',
     'https://image.aladin.co.kr/product/24532/16/cover/k762731457_1.jpg',
     1, 4, 4.0,
     '2025-01-02 09:05:00+00', '2025-03-02 01:00:00+00', NULL);

-- [01] 한강 《소년이 온다》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b64-9680-7c80-851f-cb86adc67c94',
     '9788936434206', '소년이 온다', '한강',
     '1980년 5월 광주민주화운동을 배경으로 열다섯 살 소년 동호를 중심으로 역사적 비극 속 인간의 존엄과 살아남은 자의 죄책감을 그린 소설.',
     '창비', '2014-05-19',
     'https://image.aladin.co.kr/product/4246/55/cover/8936434209_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:00:00+00', '2025-01-03 09:00:00+00', NULL);

-- [02] 한강 《흰》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b66-6b40-7e57-a456-9fdedf5bab03',
     '9791186090374', '흰', '한강',
     '흰색 사물들에 대한 단상으로 구성된 산문 소설. 죽음과 삶, 슬픔과 아름다움을 흰빛으로 관통하는 독특한 형식의 작품.',
     '난다', '2016-04-11',
     'https://image.aladin.co.kr/product/7829/56/cover/k362434536_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:02:00+00', '2025-01-03 09:02:00+00', NULL);

-- [03] 한강 《작별하지 않는다》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b68-4000-792e-9f65-442988a0a8b7',
     '9788954680011', '작별하지 않는다', '한강',
     '제주 4·3 사건의 상처와 기억을 중심으로 죽은 자와 산 자가 서로에게 건네는 위로를 담은 소설. 2023년 에밀 기메 아시아 문학상 수상작.',
     '문학동네', '2021-09-09',
     'https://image.aladin.co.kr/product/27962/56/cover/8954680011_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:04:00+00', '2025-01-03 09:04:00+00', NULL);

-- [04] 김영하 《살인자의 기억법》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b6a-14c0-77bb-b834-561c21cf69c0',
     '9788993379952', '살인자의 기억법', '김영하',
     '알츠하이머에 걸린 은퇴한 연쇄살인마가 마지막 살인을 계획하는 과정을 1인칭으로 그린 소설. 기억과 진실의 경계를 허무는 작품.',
     '복복서가', '2013-08-29',
     'https://image.aladin.co.kr/product/2183/26/cover/8993379955_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:06:00+00', '2025-01-03 09:06:00+00', NULL);

-- [05] 김영하 《빛의 제국》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b6b-e980-7cc7-945c-0a2ea5bbc11d',
     '9788954621076', '빛의 제국', '김영하',
     '남파 공작원이 임무와 정체성 사이에서 갈등하는 이야기. 냉전 이데올로기와 인간 실존의 문제를 탁월하게 그려낸 작품.',
     '문학동네', '2006-04-21',
     'https://image.aladin.co.kr/product/110/20/cover/8954621074_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:08:00+00', '2025-01-03 09:08:00+00', NULL);

-- [06] 김영하 《퀴즈쇼》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b6d-be40-7a4c-a920-0bb2cb64f722',
     '9788954621878', '퀴즈쇼', '김영하',
     '퀴즈쇼 우승자를 따라가며 한국 사회의 욕망과 허위를 날카롭게 풍자한 소설. 김영하 특유의 냉소적 유머가 돋보이는 작품.',
     '문학동네', '2007-07-20',
     'https://image.aladin.co.kr/product/764/46/cover/8954621872_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:10:00+00', '2025-01-03 09:10:00+00', NULL);

-- [07] 정유정 《7년의 밤》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b6f-9300-7d85-863c-0472d1b91665',
     '9788956057743', '7년의 밤', '정유정',
     '한 사고로 얽힌 두 가족의 7년에 걸친 집착과 복수, 그리고 구원을 그린 스릴러 소설. 정유정 작가를 대중에 알린 대표작.',
     '은행나무', '2011-02-15',
     'https://image.aladin.co.kr/product/1361/89/cover/8956057745_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:12:00+00', '2025-01-03 09:12:00+00', NULL);

-- [08] 정유정 《종의 기원》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b71-67c0-7a54-b59a-b32dfe8f76dc',
     '9788956059815', '종의 기원', '정유정',
     '악의 근원을 탐구하는 심리 스릴러. 살인 충동을 타고난 주인공이 자신의 본성과 싸우는 이야기로 인간 내면의 어둠을 정면으로 다룬 소설.',
     '은행나무', '2016-06-20',
     'https://image.aladin.co.kr/product/9047/39/cover/8956059810_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:14:00+00', '2025-01-03 09:14:00+00', NULL);

-- [09] 정유정 《28》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b73-3c80-7302-90ee-f0dc262f4e93',
     '9788956058474', '28', '정유정',
     '좀비 바이러스가 창궐한 도시를 배경으로 인간 본성과 사회 붕괴를 그린 호러 소설. 극한 상황 속 인물들의 선택이 긴장감 있게 펼쳐진다.',
     '은행나무', '2013-04-22',
     'https://image.aladin.co.kr/product/2127/24/cover/8956058474_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:16:00+00', '2025-01-03 09:16:00+00', NULL);

-- [10] 공지영 《도가니》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b75-1140-7f98-8366-bc9b92290d1a',
     '9788936434489', '도가니', '공지영',
     '광주 청각 장애인 특수학교에서 실제로 일어난 성폭력 사건을 소재로 한 소설. 사회적 약자에 대한 폭력과 침묵하는 공동체를 고발한 작품.',
     '창비', '2009-06-29',
     'https://image.aladin.co.kr/product/347/8/cover/8936434489_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:18:00+00', '2025-01-03 09:18:00+00', NULL);

-- [11] 공지영 《우리들의 행복한 시간》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b76-e600-7b3b-903c-b3fac787a322',
     '9788974745677', '우리들의 행복한 시간', '공지영',
     '사형수와 자살을 반복하는 한 여성의 만남을 통해 삶과 죽음, 용서와 구원을 다룬 소설. 수백만 독자의 마음을 울린 공지영의 대표작.',
     '오픈하우스', '2005-06-01',
     'https://image.aladin.co.kr/product/191/72/cover/8974745674_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:20:00+00', '2025-01-03 09:20:00+00', NULL);

-- [12] 최은영 《쇼코의 미소》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b78-bac0-765e-b228-4798e168acea',
     '9788954637268', '쇼코의 미소', '최은영',
     '타인의 고통에 공감하는 능력과 그 공감이 만들어내는 관계를 섬세하게 그린 단편 소설집. 최은영의 데뷔작으로 독자들의 큰 사랑을 받았다.',
     '문학동네', '2016-03-30',
     'https://image.aladin.co.kr/product/7567/52/cover/8954637264_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:22:00+00', '2025-01-03 09:22:00+00', NULL);

-- [13] 최은영 《내게 무해한 사람》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b7a-8f80-7fc4-8e82-da6ff3050dad',
     '9788954645058', '내게 무해한 사람', '최은영',
     '상처받은 사람들이 서로에게 위로가 되는 과정을 담담하고 따뜻하게 그린 소설집. 최은영 작가의 문학적 성취를 확인할 수 있는 작품.',
     '문학동네', '2018-10-25',
     'https://image.aladin.co.kr/product/17006/79/cover/8954645054_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:24:00+00', '2025-01-03 09:24:00+00', NULL);

-- [14] 김혜진 《딸에 대하여》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b7c-6440-7d56-99bf-b11da19cba54',
     '9788937436208', '딸에 대하여', '김혜진',
     '성소수자 딸과 그녀를 이해하려는 어머니를 통해 한국 사회의 편견과 가족의 의미를 탐구한 소설.',
     '민음사', '2017-09-15',
     'https://image.aladin.co.kr/product/11620/65/cover/8937436205_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:26:00+00', '2025-01-03 09:26:00+00', NULL);

-- [15] 김혜진 《중앙역》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b7e-3900-7132-8181-1f9e45116dc8',
     '9788937465079', '중앙역', '김혜진',
     '지하도 노숙자들의 삶을 통해 현대 사회의 그늘과 인간 존엄을 이야기하는 소설. 냉정하지만 따뜻한 시선으로 사회의 밑바닥을 조명한다.',
     '민음사', '2019-03-11',
     'https://image.aladin.co.kr/product/18892/7/cover/8937465078_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:28:00+00', '2025-01-03 09:28:00+00', NULL);

-- [16] 김초엽 《우리가 빛의 속도로 갈 수 없다면》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b80-0dc0-7f62-a918-c4e1333e83e7',
     '9788961566469', '우리가 빛의 속도로 갈 수 없다면', '김초엽',
     '과학적 상상력과 인문학적 감수성을 결합한 SF 단편 소설집. 기술 발전 속에서도 변하지 않는 인간의 감정과 관계를 탁월하게 그려낸 데뷔작.',
     '허블', '2019-06-24',
     'https://image.aladin.co.kr/product/19461/66/cover/k472632633_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:30:00+00', '2025-01-03 09:30:00+00', NULL);

-- [17] 김초엽 《지구 끝의 온실》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b81-e280-7f84-a004-0924df80c099',
     '9791168410657', '지구 끝의 온실', '김초엽',
     '멸망한 세계에서 살아남은 사람들이 식물과 함께 만들어가는 이야기. 생태적 상상력과 인간 생존의 의지를 결합한 장편 SF 소설.',
     '자이언트북스', '2021-07-20',
     'https://image.aladin.co.kr/product/27355/93/cover/k562731545_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:32:00+00', '2025-01-03 09:32:00+00', NULL);

-- [18] 이민진 《파친코》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b83-b740-7355-b7b8-0e3c2fb18e06',
     '9788930087100', '파친코', '이민진',
     '20세기 초 한국에서 일본으로 건너간 재일 한인 가족 4대의 삶을 그린 대하소설. 정체성과 차별, 생존에 관한 이야기를 장대하게 펼쳐낸 작품.',
     '문학사상', '2017-02-05',
     'https://image.aladin.co.kr/product/9157/38/cover/8930087108_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:34:00+00', '2025-01-03 09:34:00+00', NULL);

-- [19] 김훈 《남한산성》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b85-8c00-7acc-8a8d-fc0554aaf5f1',
     '9788960900288', '남한산성', '김훈',
     '1636년 병자호란 당시 45일간의 남한산성 농성을 배경으로 척화와 주화 사이에서 갈등하는 조선의 운명을 그린 역사 소설.',
     '학고재', '2007-04-09',
     'https://image.aladin.co.kr/product/621/9/cover/8960900281_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:36:00+00', '2025-01-03 09:36:00+00', NULL);

-- [20] 김훈 《칼의 노래》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b87-60c0-7f61-8588-accae290ff80',
     '9788984315051', '칼의 노래', '김훈',
     '이순신 장군의 시각에서 임진왜란을 그린 소설. 전쟁의 비참함과 인간의 고독, 한 지휘관의 내면을 깊이 파헤친 역사 소설의 걸작.',
     '생각의나무', '2001-05-28',
     'https://image.aladin.co.kr/product/8/41/cover/898431505X_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:38:00+00', '2025-01-03 09:38:00+00', NULL);

-- [21] 정은궐 《해를 품은 달》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b89-3580-78d4-bb1d-8df8e2309d25',
     '9788994058108', '해를 품은 달', '정은궐',
     '조선 시대를 배경으로 한 왕과 무녀의 운명적 사랑 이야기. 출판 후 드라마로도 제작되어 큰 인기를 끈 로맨스 역사 소설.',
     '파란미디어', '2009-07-01',
     'https://image.aladin.co.kr/product/348/7/cover/8994058109_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:40:00+00', '2025-01-03 09:40:00+00', NULL);

-- [22] 정은궐 《성균관 유생들의 나날》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b8b-0a40-7985-b944-ab27775b8a02',
     '9788994058023', '성균관 유생들의 나날', '정은궐',
     '조선 시대 성균관을 배경으로 청춘들의 우정과 사랑, 성장을 그린 역사 로맨스 소설. 드라마화되어 많은 사랑을 받은 작품.',
     '파란미디어', '2007-03-01',
     'https://image.aladin.co.kr/product/348/8/cover/8994058028_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:42:00+00', '2025-01-03 09:42:00+00', NULL);

-- [23] 조창인 《가시고기》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b8c-df00-7b24-afa5-45a5eff6c534',
     '9788975744129', '가시고기', '조창인',
     '백혈병에 걸린 아들을 위해 모든 것을 희생하는 아버지의 지극한 사랑을 그린 소설. 부성애의 숭고함을 담담하지만 진하게 묘사한 작품.',
     '해냄', '1998-07-10',
     'https://image.aladin.co.kr/product/3/96/cover/8975744124_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:44:00+00', '2025-01-03 09:44:00+00', NULL);

-- [24] 김난도 외 《트렌드 코리아 2025》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b8e-b3c0-75e3-aa50-4fef21346957',
     '9791161572123', '트렌드 코리아 2025', '김난도 외',
     '서울대 소비트렌드분석센터가 매년 예측하는 한국 소비 트렌드 보고서. 2025년 주목해야 할 10대 키워드를 분석한 베스트셀러 시리즈.',
     '미래의창', '2024-10-01',
     'https://image.aladin.co.kr/product/35000/11/cover/k312931555_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:46:00+00', '2025-01-03 09:46:00+00', NULL);

-- [25] 헤르만 헤세 《데미안》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b90-8880-7a74-9dc1-bfd57fe9d21e',
     '9788937460449', '데미안', '헤르만 헤세',
     '소년 싱클레어가 데미안을 만나며 자아를 찾아가는 성장 소설. 자기만의 길을 찾아가는 인간의 내면 여정을 탁월하게 그려낸 세계문학의 고전.',
     '민음사', '2000-02-28',
     'https://image.aladin.co.kr/product/5/75/cover/8937460440_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:48:00+00', '2025-01-03 09:48:00+00', NULL);

-- [26] 앙투안 드 생텍쥐페리 《어린 왕자》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b92-5d40-703c-8386-3c7fbfb2638f',
     '9788954634465', '어린 왕자', '앙투안 드 생텍쥐페리',
     '사막에 불시착한 비행사가 만난 소행성 B612의 어린 왕자 이야기. 어른들이 잃어버린 순수함과 본질에 대한 통찰을 담은 영원한 고전.',
     '문학동네', '2015-09-25',
     'https://image.aladin.co.kr/product/6734/57/cover/8954634461_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:50:00+00', '2025-01-03 09:50:00+00', NULL);

-- [27] 조지 오웰 《1984》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b94-3200-73e3-8720-e6ddfd7913ba',
     '9788937460777', '1984', '조지 오웰',
     '전체주의 국가의 감시와 통제를 그린 디스토피아 소설. 빅브라더, 이중사고 등 현대에도 유효한 개념들을 탄생시킨 20세기 최고의 정치 소설.',
     '민음사', '2003-06-05',
     'https://image.aladin.co.kr/product/8/84/cover/8937460777_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:52:00+00', '2025-01-03 09:52:00+00', NULL);

-- [28] 파울로 코엘료 《연금술사》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b96-06c0-7b8f-a9dd-ab17d7b97091',
     '9788932908137', '연금술사', '파울로 코엘료',
     '자신의 꿈을 찾아 여행하는 양치기 소년 산티아고의 이야기. 삶의 의미와 자아실현에 대한 우화로 전 세계 수억 부가 팔린 현대의 고전.',
     '민음사', '2001-04-25',
     'https://image.aladin.co.kr/product/6/15/cover/8932908133_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:54:00+00', '2025-01-03 09:54:00+00', NULL);

-- [29] 어니스트 헤밍웨이 《노인과 바다》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b97-db80-73b2-b2e3-a639676cfe86',
     '9788932906270', '노인과 바다', '어니스트 헤밍웨이',
     '노련한 어부 산티아고가 거대한 청새치와 사흘 밤낮을 사투하는 이야기. 인간의 의지와 패배, 그리고 영광을 담담하게 그린 헤밍웨이의 대표작.',
     '민음사', '2012-04-30',
     'https://image.aladin.co.kr/product/6/24/cover/8932906270_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:56:00+00', '2025-01-03 09:56:00+00', NULL);

-- [30] F. 스콧 피츠제럴드 《위대한 개츠비》
INSERT INTO books
(id, isbn, title, author, description, publisher, published_date,
 thumbnail_url, review_count, total_score, rating,
 created_at, updated_at, deleted_at)
VALUES
    ('01942b99-b040-7e50-b668-1b55462a58d4',
     '9788937461057', '위대한 개츠비', 'F. 스콧 피츠제럴드',
     '1920년대 미국 황금기를 배경으로 한 개츠비의 허망한 사랑과 꿈 이야기. 아메리칸 드림의 허상을 화려하고도 비극적으로 그린 미국 문학의 정수.',
     '민음사', '2009-07-15',
     'https://image.aladin.co.kr/product/348/6/cover/8937461056_1.jpg',
     0, 0, 0.0,
     '2025-01-03 09:58:00+00', '2025-01-03 09:58:00+00', NULL);


-- ============================
-- REVIEWS (15건)
-- 제약: uq_active_review (user_id, book_id) WHERE deleted_at IS NULL
-- ============================
-- [01] 김민준 → 채식주의자 (★5  👍3  💬3)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01946967-d500-772f-91cd-e06496da1dac',
     '019435e8-3d00-7a3b-8199-c6b41c80317f', '0194263e-3a80-7972-8208-ba3c6c031199',
     '한강 작가의 문체가 정말 독특하고 아름답습니다. 채식주의자를 통해 인간 내면의 어두운 부분을 섬세하게 그려냈어요. 맨부커 수상이 전혀 놀랍지 않을 만큼 완성도 높은 작품입니다. 강력 추천합니다.',
     5, 3, 3,
     '2025-01-15 10:00:00+00', '2025-01-15 10:00:00+00', NULL);

-- [02] 김민준 → 82년생 김지영 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01948403-5b00-7cf3-806a-79c7de8a774b',
     '019435e8-3d00-7a3b-8199-c6b41c80317f', '0194263f-24e0-707a-8dfe-2a2217fc695a',
     '82년생 김지영을 읽으며 많은 공감을 했습니다. 우리 사회에서 여성으로서 겪는 일상적 차별을 담담하게 서술한 작품이에요. 특별한 사건이 아닌 평범한 일상 속 불평등이 더 크게 느껴졌습니다.',
     4, 0, 0,
     '2025-01-20 14:00:00+00', '2025-01-20 14:00:00+00', NULL);

-- [03] 김민준 → 아몬드 (★5  👍2  💬2)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01949e31-0400-7c24-8a37-dbb1ce4a2bbd',
     '019435e8-3d00-7a3b-8199-c6b41c80317f', '01942640-0f40-73b8-a687-7991815ef6d1',
     '아몬드는 감정을 느끼지 못하는 주인공을 통해 오히려 인간의 감정이 얼마나 소중한지 깨닫게 해주는 책입니다. 읽는 내내 따뜻하면서도 묵직한 감동이 있었어요. 청소년 소설이지만 어른들에게도 꼭 추천하고 싶어요.',
     5, 2, 2,
     '2025-01-25 16:00:00+00', '2025-01-25 16:00:00+00', NULL);

-- [04] 이서연 → 채식주의자 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('019478a3-fa80-7b2b-95c6-aa1d6c307511',
     '01944652-8240-7bdd-8fac-4ee446685257', '0194263e-3a80-7972-8208-ba3c6c031199',
     '채식주의자는 불편하지만 그 불편함 속에 깊은 메시지가 있습니다. 한강 특유의 문학적 세계관이 잘 드러나는 작품이에요. 세 개의 단편이 유기적으로 연결되는 구성이 인상적입니다.',
     4, 0, 0,
     '2025-01-18 09:00:00+00', '2025-01-18 09:00:00+00', NULL);

-- [05] 이서연 → 완전한 행복 (★3  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('0194d5c4-4f80-7472-8dc7-b35e27cd8130',
     '01944652-8240-7bdd-8fac-4ee446685257', '01942640-f9a0-706c-8cb9-c18a8fadc1a6',
     '완전한 행복은 제목과 달리 읽는 내내 긴장감이 넘칩니다. 정유정 작가의 스토리텔링은 역시 최고예요. 다만 결말이 조금 아쉬워서 별 3개를 드렸습니다. 이전 작품들과 비교하면 살짝 아쉬운 느낌이에요.',
     3, 0, 0,
     '2025-02-05 11:00:00+00', '2025-02-05 11:00:00+00', NULL);

-- [06] 이서연 → 불편한 편의점 (★5  👍3  💬2)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('0194f05f-d580-7f50-958a-c3dec37459ee',
     '01944652-8240-7bdd-8fac-4ee446685257', '01942641-e400-7b74-ace2-8223a65ed389',
     '불편한 편의점을 읽으면서 마음이 따뜻해졌어요. 우리 주변의 평범한 사람들의 이야기가 이렇게 감동적일 수 있다는 걸 느꼈습니다. 독고 씨와 편의점 사람들의 성장이 보는 내내 뭉클했어요.',
     5, 3, 2,
     '2025-02-10 15:00:00+00', '2025-02-10 15:00:00+00', NULL);

-- [07] 박도현 → 82년생 김지영 (★4  👍2  💬2)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01948d06-7c00-71a2-9850-ba9f17be3111',
     '019459cb-8e20-7392-af22-582a23b8c1e9', '0194263f-24e0-707a-8dfe-2a2217fc695a',
     '82년생 김지영을 읽으며 현실적인 문제들을 다시 생각해보게 됐어요. 모든 사람이 읽어야 할 책이라고 생각합니다. 특히 육아 부분에서 정말 많은 공감이 됐어요.',
     4, 2, 2,
     '2025-01-22 08:00:00+00', '2025-01-22 08:00:00+00', NULL);

-- [08] 박도현 → 아몬드 (★4  👍2  💬2)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('0194addb-0680-718c-b63d-59045be6128e',
     '019459cb-8e20-7392-af22-582a23b8c1e9', '01942640-0f40-73b8-a687-7991815ef6d1',
     '아몬드는 청소년 소설이지만 어른들도 꼭 읽어볼 만한 작품입니다. 공감과 이해에 대해 깊이 생각하게 만드는 소설이에요. 곤이라는 캐릭터가 특히 인상에 남아요.',
     4, 2, 2,
     '2025-01-28 17:00:00+00', '2025-01-28 17:00:00+00', NULL);

-- [09] 박도현 → 달러구트 꿈 백화점 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('0194cbae-8600-7580-90ed-e8e99a8dca03',
     '019459cb-8e20-7392-af22-582a23b8c1e9', '01942642-ce60-78b8-8e1b-b2f86b65a6a4',
     '달러구트 꿈 백화점은 독특한 세계관이 매력적이에요. 꿈을 사고파는 백화점이라는 아이디어가 참신하고 이야기도 따뜻해요. 각 챕터마다 독립된 이야기가 이어지는 구성도 좋았습니다.',
     4, 0, 0,
     '2025-02-03 12:00:00+00', '2025-02-03 12:00:00+00', NULL);

-- [10] 최유진 → 채식주의자 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('0194e500-7500-7ce9-aeb3-ecf40b1f9163',
     '0194c266-bae0-71a3-af67-19acad3c2d6d', '0194263e-3a80-7972-8208-ba3c6c031199',
     '채식주의자는 한번 읽기 시작하면 멈출 수가 없어요. 서사 구조가 독특하고 각 인물의 시점이 잘 살아있습니다. 영혜를 바라보는 세 인물의 시선이 각각 달라서 흥미로웠어요.',
     4, 0, 0,
     '2025-02-08 10:00:00+00', '2025-02-08 10:00:00+00', NULL);

-- [11] 최유진 → 완전한 행복 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('019509b1-c480-7759-87fd-26de89463e85',
     '0194c266-bae0-71a3-af67-19acad3c2d6d', '01942640-f9a0-706c-8cb9-c18a8fadc1a6',
     '완전한 행복에서 정유정 작가는 또 한번 놀라운 서스펜스를 선보입니다. 심리 묘사가 탁월하고 읽는 내내 몰입하게 됩니다. 주인공의 뒤틀린 심리가 섬뜩하면서도 이해가 가는 부분이 있어요.',
     4, 0, 0,
     '2025-02-15 13:00:00+00', '2025-02-15 13:00:00+00', NULL);

-- [12] 최유진 → 불편한 편의점 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01952295-d680-7f91-9839-e844ec1b8ca1',
     '0194c266-bae0-71a3-af67-19acad3c2d6d', '01942641-e400-7b74-ace2-8223a65ed389',
     '불편한 편의점 2를 기다리고 있어요! 1편에서 각 인물들의 이야기가 모두 사랑스럽고 진정성이 넘쳤어요. 현대 도시 사람들의 외로움과 연대를 이렇게 잘 표현한 소설이 드물죠.',
     4, 0, 0,
     '2025-02-20 09:00:00+00', '2025-02-20 09:00:00+00', NULL);

-- [13] 정하은 → 82년생 김지영 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01952d50-6b80-7142-92c3-6ed08d5288f1',
     '0195042f-db00-7e46-8590-67e08b9d2434', '0194263f-24e0-707a-8dfe-2a2217fc695a',
     '82년생 김지영을 뒤늦게 읽었는데 너무 늦었나 싶을 정도로 좋았어요. 우리 모두의 이야기이기에 더 울림이 크게 느껴졌습니다. 여성이라면 공감 백 퍼센트, 남성이라면 이해의 폭이 넓어지는 책이에요.',
     4, 0, 0,
     '2025-02-22 11:00:00+00', '2025-02-22 11:00:00+00', NULL);

-- [14] 정하은 → 아몬드 (★5  👍3  💬2)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01953d68-4b00-7d45-a795-d3dea0ee89ae',
     '0195042f-db00-7e46-8590-67e08b9d2434', '01942640-0f40-73b8-a687-7991815ef6d1',
     '아몬드는 강렬한 인상을 남기는 소설이에요. 주인공 윤재의 성장 과정을 따라가다 보면 어느새 책의 마지막 페이지에 도달하게 됩니다. 손원평 작가의 다른 작품들도 기대됩니다.',
     5, 3, 2,
     '2025-02-25 14:00:00+00', '2025-02-25 14:00:00+00', NULL);

-- [15] 정하은 → 완전한 행복 (★4  👍0  💬0)
INSERT INTO reviews
(id, user_id, book_id, content, rating, like_count, comment_count,
 created_at, updated_at, deleted_at)
VALUES
    ('01955126-0100-7e2a-9725-073cdc98d2c1',
     '0195042f-db00-7e46-8590-67e08b9d2434', '01942640-f9a0-706c-8cb9-c18a8fadc1a6',
     '완전한 행복은 스릴러지만 단순한 스릴러가 아니에요. 가족과 행복에 대한 철학적 질문을 던지는 작품입니다. 행복이란 무엇인가를 다시 한번 생각해보게 만드는 묵직한 소설이에요.',
     4, 0, 0,
     '2025-03-01 10:00:00+00', '2025-03-01 10:00:00+00', NULL);

-- ============================
-- REVIEW_LIKES (15건)
-- 제약: uq_review_like (review_id, user_id) UNIQUE
-- ============================
INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01946b8d-2600-7313-8473-9774b45ed1f0',
     '01946967-d500-772f-91cd-e06496da1dac', '01944652-8240-7bdd-8fac-4ee446685257',
     '2025-01-15 20:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('0194699e-c380-7a94-b179-f3a23a578a8e',
     '01946967-d500-772f-91cd-e06496da1dac', '019459cb-8e20-7392-af22-582a23b8c1e9',
     '2025-01-15 11:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01946a7a-7d80-7fc3-b6bd-8689146d3f31',
     '01946967-d500-772f-91cd-e06496da1dac', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '2025-01-15 15:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01949f0c-be00-7ddd-9853-fcf519db3ad0',
     '01949e31-0400-7c24-8a37-dbb1ce4a2bbd', '01944652-8240-7bdd-8fac-4ee446685257',
     '2025-01-25 20:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01949f43-ac80-7741-b562-10b7a2bc372f',
     '01949e31-0400-7c24-8a37-dbb1ce4a2bbd', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '2025-01-25 21:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('0194f1a9-6c80-729a-96bc-c1545ec42e08',
     '0194f05f-d580-7f50-958a-c3dec37459ee', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '2025-02-10 21:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('0194f13b-8f80-7ab9-acea-9fbf4458a885',
     '0194f05f-d580-7f50-958a-c3dec37459ee', '019459cb-8e20-7392-af22-582a23b8c1e9',
     '2025-02-10 19:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('0194f2bc-1500-7a5e-a6fc-009c12476f57',
     '0194f05f-d580-7f50-958a-c3dec37459ee', '0195042f-db00-7e46-8590-67e08b9d2434',
     '2025-02-11 02:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01948f62-bb80-72bc-aeaa-037588bd6407',
     '01948d06-7c00-71a2-9850-ba9f17be3111', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '2025-01-22 19:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01948de2-3600-729d-9848-ff7d7656af72',
     '01948d06-7c00-71a2-9850-ba9f17be3111', '0195042f-db00-7e46-8590-67e08b9d2434',
     '2025-01-22 12:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('0194aeed-af00-7fd5-a8f5-c18aece66fa2',
     '0194addb-0680-718c-b63d-59045be6128e', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '2025-01-28 22:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('0194b06e-3480-78e9-abd0-b84b3838b326',
     '0194addb-0680-718c-b63d-59045be6128e', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '2025-01-29 05:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01953eb1-e200-7d7c-b1a9-fb8ec4b032cc',
     '01953d68-4b00-7d45-a795-d3dea0ee89ae', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '2025-02-25 20:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01953d9f-3980-73aa-820d-ee28d261a7ab',
     '01953d68-4b00-7d45-a795-d3dea0ee89ae', '01944652-8240-7bdd-8fac-4ee446685257',
     '2025-02-25 15:00:00+00');

INSERT INTO review_likes (id, review_id, user_id, created_at)
VALUES
    ('01953eb1-e200-766b-843c-6f20448aaa9e',
     '01953d68-4b00-7d45-a795-d3dea0ee89ae', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '2025-02-25 20:00:00+00');

-- ============================
-- COMMENTS (13건)
-- ============================
INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('01946c9f-ce80-7e9c-a44c-ad8ff16287e4',
     '01946967-d500-772f-91cd-e06496da1dac', '01944652-8240-7bdd-8fac-4ee446685257',
     '정말 좋은 리뷰예요! 채식주의자 저도 너무 좋았어요. 한강 작가 특유의 문체가 정말 독특하죠.',
     NULL, '2025-01-16 01:00:00+00', '2025-01-16 01:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('01946e20-5400-7366-9ff3-67aca7cad415',
     '01946967-d500-772f-91cd-e06496da1dac', '019459cb-8e20-7392-af22-582a23b8c1e9',
     '저도 같은 생각이에요. 맨부커 수상이 전혀 놀랍지 않은 작품이죠.',
     NULL, '2025-01-16 08:00:00+00', '2025-01-16 08:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('01946f32-fc80-7e27-a924-7c2cea1fca65',
     '01946967-d500-772f-91cd-e06496da1dac', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '채식주의자 꼭 읽어보세요! 불편하지만 그게 이 책의 매력인 것 같아요.',
     NULL, '2025-01-16 13:00:00+00', '2025-01-16 13:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('0194a4d7-e580-7249-88ef-b40743cf2fde',
     '01949e31-0400-7c24-8a37-dbb1ce4a2bbd', '01944652-8240-7bdd-8fac-4ee446685257',
     '아몬드 정말 명작이죠? 저도 읽고 한동안 여운이 남았어요.',
     NULL, '2025-01-26 23:00:00+00', '2025-01-26 23:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('0194a1d6-da80-7beb-a27e-9a9a8fb5d27b',
     '01949e31-0400-7c24-8a37-dbb1ce4a2bbd', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '공감해요! 윤재 캐릭터가 너무 인상적이에요. 곤이랑의 우정도 좋았고요.',
     NULL, '2025-01-26 09:00:00+00', '2025-01-26 09:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('0194f43c-9a80-7bf3-9b6b-75b195a76d79',
     '0194f05f-d580-7f50-958a-c3dec37459ee', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '저도 불편한 편의점 정말 좋아해요! 독고 씨 이야기에서 눈물 흘렸어요.',
     NULL, '2025-02-11 09:00:00+00', '2025-02-11 09:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('0194f62a-fd00-75ca-bfd4-2f79382567b8',
     '0194f05f-d580-7f50-958a-c3dec37459ee', '019459cb-8e20-7392-af22-582a23b8c1e9',
     '정말 감동적이죠. 편의점이라는 공간이 이렇게 따뜻할 수 있다는 게 신기했어요.',
     NULL, '2025-02-11 18:00:00+00', '2025-02-11 18:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('01948f2b-cd00-7827-85d1-75b67e570ddf',
     '01948d06-7c00-71a2-9850-ba9f17be3111', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '82년생 김지영, 정말 공감 가는 책이에요. 리뷰도 너무 잘 쓰셨어요.',
     NULL, '2025-01-22 18:00:00+00', '2025-01-22 18:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('01948e19-2480-7dc7-89c8-26f71c11f735',
     '01948d06-7c00-71a2-9850-ba9f17be3111', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '맞아요, 우리 모두의 이야기 같아서 더 와닿았어요. 특히 육아 부분이요.',
     NULL, '2025-01-22 13:00:00+00', '2025-01-22 13:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('0194b06e-3480-7cac-9b04-ab3aae340454',
     '0194addb-0680-718c-b63d-59045be6128e', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '아몬드 명작이에요! 손원평 작가 다른 작품도 추천해드려요.',
     NULL, '2025-01-29 05:00:00+00', '2025-01-29 05:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('0194af24-9d80-7628-a623-093261b1cd22',
     '0194addb-0680-718c-b63d-59045be6128e', '0195042f-db00-7e46-8590-67e08b9d2434',
     '손원평 작가 팬이에요. 리뷰 잘 읽었습니다! 덕분에 다시 읽고 싶어졌어요.',
     NULL, '2025-01-28 23:00:00+00', '2025-01-28 23:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('0195440f-2c80-7877-be26-25ee405cacec',
     '01953d68-4b00-7d45-a795-d3dea0ee89ae', '019435e8-3d00-7a3b-8199-c6b41c80317f',
     '아몬드 진짜 최고예요! 리뷰도 너무 잘 쓰셨어요.',
     NULL, '2025-02-26 21:00:00+00', '2025-02-26 21:00:00+00');

INSERT INTO comments
(id, review_id, user_id, content, deleted_at, created_at, updated_at)
VALUES
    ('01953dd6-2800-7ae2-8754-d0d2b88139b9',
     '01953d68-4b00-7d45-a795-d3dea0ee89ae', '0194c266-bae0-71a3-af67-19acad3c2d6d',
     '동감이에요. 주변 사람들한테 꼭 추천하고 싶은 책이에요.',
     NULL, '2025-02-25 16:00:00+00', '2025-02-25 16:00:00+00');

-- ============================
-- NOTIFICATIONS (10건)
-- type: REVIEW_LIKE | REVIEW_COMMENT
-- ============================
INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('01947346-b000-7e28-b00e-61c48976e334',
     '019435e8-3d00-7a3b-8199-c6b41c80317f', '01946967-d500-772f-91cd-e06496da1dac',
     '한강 작가의 문체가 정말 독특하고 아름답습니다. 채식주의자를 통해 인간 내면의 어두운 부분을 섬세하게 그려냈어요. 맨부커 수상이 전혀 놀랍지 않을 만큼 완성도 높은 작품입니다. 강력 추천합니다.',
     '이서연님이 회원님의 리뷰에 좋아요를 눌렀습니다.',
     FALSE, 'REVIEW_LIKE',
     '2025-01-17 08:00:00+00', '2025-01-17 08:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('0194a27b-a600-7c4c-95c5-6f5ba4161293',
     '019435e8-3d00-7a3b-8199-c6b41c80317f', '01949e31-0400-7c24-8a37-dbb1ce4a2bbd',
     '아몬드는 감정을 느끼지 못하는 주인공을 통해 오히려 인간의 감정이 얼마나 소중한지 깨닫게 해주는 책입니다. 읽는 내내 따뜻하면서도 묵직한 감동이 있었어요. 청소년 소설이지만 어른들에게도 꼭 추천하고 싶어요.',
     '이서연님이 회원님의 리뷰에 좋아요를 눌렀습니다.',
     FALSE, 'REVIEW_LIKE',
     '2025-01-26 12:00:00+00', '2025-01-26 12:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('0194f285-2680-74b2-8a1f-41b26f4cc69a',
     '01944652-8240-7bdd-8fac-4ee446685257', '0194f05f-d580-7f50-958a-c3dec37459ee',
     '불편한 편의점을 읽으면서 마음이 따뜻해졌어요. 우리 주변의 평범한 사람들의 이야기가 이렇게 감동적일 수 있다는 걸 느꼈습니다. 독고 씨와 편의점 사람들의 성장이 보는 내내 뭉클했어요.',
     '김민준님이 회원님의 리뷰에 좋아요를 눌렀습니다.',
     TRUE, 'REVIEW_LIKE',
     '2025-02-11 01:00:00+00', '2025-02-11 01:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('0194b4b8-d680-700d-ae36-c19cf42d47cc',
     '019459cb-8e20-7392-af22-582a23b8c1e9', '0194addb-0680-718c-b63d-59045be6128e',
     '아몬드는 청소년 소설이지만 어른들도 꼭 읽어볼 만한 작품입니다. 공감과 이해에 대해 깊이 생각하게 만드는 소설이에요. 곤이라는 캐릭터가 특히 인상에 남아요.',
     '김민준님이 회원님의 리뷰에 좋아요를 눌렀습니다.',
     FALSE, 'REVIEW_LIKE',
     '2025-01-30 01:00:00+00', '2025-01-30 01:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('019547eb-f180-7436-a009-9a57f8cda88b',
     '0195042f-db00-7e46-8590-67e08b9d2434', '01953d68-4b00-7d45-a795-d3dea0ee89ae',
     '아몬드는 강렬한 인상을 남기는 소설이에요. 주인공 윤재의 성장 과정을 따라가다 보면 어느새 책의 마지막 페이지에 도달하게 됩니다. 손원평 작가의 다른 작품들도 기대됩니다.',
     '최유진님이 회원님의 리뷰에 좋아요를 눌렀습니다.',
     TRUE, 'REVIEW_LIKE',
     '2025-02-27 15:00:00+00', '2025-02-27 15:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('01946c68-e000-781f-86cf-6f57e9a1fa6f',
     '019435e8-3d00-7a3b-8199-c6b41c80317f', '01946967-d500-772f-91cd-e06496da1dac',
     '한강 작가의 문체가 정말 독특하고 아름답습니다. 채식주의자를 통해 인간 내면의 어두운 부분을 섬세하게 그려냈어요. 맨부커 수상이 전혀 놀랍지 않을 만큼 완성도 높은 작품입니다. 강력 추천합니다.',
     '이서연님이 회원님의 리뷰에 댓글을 달았습니다.',
     TRUE, 'REVIEW_COMMENT',
     '2025-01-16 00:00:00+00', '2025-01-16 00:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('0194a76b-1380-74c6-a8e4-8c69d777a477',
     '019435e8-3d00-7a3b-8199-c6b41c80317f', '01949e31-0400-7c24-8a37-dbb1ce4a2bbd',
     '아몬드는 감정을 느끼지 못하는 주인공을 통해 오히려 인간의 감정이 얼마나 소중한지 깨닫게 해주는 책입니다. 읽는 내내 따뜻하면서도 묵직한 감동이 있었어요. 청소년 소설이지만 어른들에게도 꼭 추천하고 싶어요.',
     '이서연님이 회원님의 리뷰에 댓글을 달았습니다.',
     FALSE, 'REVIEW_COMMENT',
     '2025-01-27 11:00:00+00', '2025-01-27 11:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('0194f7e2-7100-79be-89c8-1e5f32ebd689',
     '01944652-8240-7bdd-8fac-4ee446685257', '0194f05f-d580-7f50-958a-c3dec37459ee',
     '불편한 편의점을 읽으면서 마음이 따뜻해졌어요. 우리 주변의 평범한 사람들의 이야기가 이렇게 감동적일 수 있다는 걸 느꼈습니다. 독고 씨와 편의점 사람들의 성장이 보는 내내 뭉클했어요.',
     '김민준님이 회원님의 리뷰에 댓글을 달았습니다.',
     TRUE, 'REVIEW_COMMENT',
     '2025-02-12 02:00:00+00', '2025-02-12 02:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('0194929a-b500-7c33-a285-2f98295b4715',
     '019459cb-8e20-7392-af22-582a23b8c1e9', '01948d06-7c00-71a2-9850-ba9f17be3111',
     '82년생 김지영을 읽으며 현실적인 문제들을 다시 생각해보게 됐어요. 모든 사람이 읽어야 할 책이라고 생각합니다. 특히 육아 부분에서 정말 많은 공감이 됐어요.',
     '김민준님이 회원님의 리뷰에 댓글을 달았습니다.',
     FALSE, 'REVIEW_COMMENT',
     '2025-01-23 10:00:00+00', '2025-01-23 10:00:00+00');

INSERT INTO notifications
(id, user_id, review_id, review_content, message, confirmed, type,
 created_at, updated_at)
VALUES
    ('01954521-d500-7eb2-a655-1bad00257ad1',
     '0195042f-db00-7e46-8590-67e08b9d2434', '01953d68-4b00-7d45-a795-d3dea0ee89ae',
     '아몬드는 강렬한 인상을 남기는 소설이에요. 주인공 윤재의 성장 과정을 따라가다 보면 어느새 책의 마지막 페이지에 도달하게 됩니다. 손원평 작가의 다른 작품들도 기대됩니다.',
     '김민준님이 회원님의 리뷰에 댓글을 달았습니다.',
     TRUE, 'REVIEW_COMMENT',
     '2025-02-27 02:00:00+00', '2025-02-27 02:00:00+00');

-- ============================
-- DASHBOARD_BATCH_EXECUTION (5건)
-- ============================
INSERT INTO dashboard_batch_execution
(id, job_name, period_type, status, started_at, finished_at, created_at)
VALUES
    ('01954f00-b000-752f-813f-1b607d154385',
     'bookRatingBatchJob', 'DAILY', 'COMPLETED',
     '2025-03-01 00:00:00+00', '2025-03-01 00:01:30+00', '2025-03-01 00:00:00+00');

INSERT INTO dashboard_batch_execution
(id, job_name, period_type, status, started_at, finished_at, created_at)
VALUES
    ('01955427-0c00-71ca-973b-13adedd96831',
     'bookRatingBatchJob', 'DAILY', 'COMPLETED',
     '2025-03-02 00:00:00+00', '2025-03-02 00:01:25+00', '2025-03-02 00:00:00+00');

INSERT INTO dashboard_batch_execution
(id, job_name, period_type, status, started_at, finished_at, created_at)
VALUES
    ('0195594d-6800-7e0f-b53a-020efc3e058b',
     'bookRatingBatchJob', 'WEEKLY', 'COMPLETED',
     '2025-03-03 00:00:00+00', '2025-03-03 00:03:10+00', '2025-03-03 00:00:00+00');

INSERT INTO dashboard_batch_execution
(id, job_name, period_type, status, started_at, finished_at, created_at)
VALUES
    ('01954f37-9e80-7ce8-8f53-2fcd4eb93eff',
     'popularReviewBatchJob', 'DAILY', 'FAILED',
     '2025-03-01 01:00:00+00', '2025-03-01 01:00:45+00', '2025-03-01 01:00:00+00');

INSERT INTO dashboard_batch_execution
(id, job_name, period_type, status, started_at, finished_at, created_at)
VALUES
    ('0195545d-fa80-70ed-b831-4f2e3da9c2a9',
     'popularReviewBatchJob', 'DAILY', 'COMPLETED',
     '2025-03-02 01:00:00+00', '2025-03-02 01:01:12+00', '2025-03-02 01:00:00+00');
