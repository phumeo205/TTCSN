USE [UD_QUIZZI];
GO

-- =======================================================
-- BƯỚC 1: XÓA DỮ LIỆU CŨ & RESET ID
-- =======================================================
DELETE FROM dbo.Results;
DELETE FROM dbo.Question; -- Bảng Question số ít
DELETE FROM dbo.Exams;
DELETE FROM dbo.Users;

DBCC CHECKIDENT ('dbo.Results', RESEED, 0);
DBCC CHECKIDENT ('dbo.Question', RESEED, 0);
DBCC CHECKIDENT ('dbo.Exams', RESEED, 0);
DBCC CHECKIDENT ('dbo.Users', RESEED, 0);
GO

-- =======================================================
-- BƯỚC 2: TẠO 40 HỌC SINH (TÊN TIẾNG VIỆT THẬT)
-- =======================================================
DECLARE @i INT = 1;
DECLARE @Ho NVARCHAR(20);
DECLARE @TenDem NVARCHAR(20);
DECLARE @Ten NVARCHAR(20);
DECLARE @FullName NVARCHAR(50);

WHILE @i <= 40
BEGIN
    -- Random Họ
    IF @i % 5 = 0 SET @Ho = N'Nguyễn';
    ELSE IF @i % 5 = 1 SET @Ho = N'Trần';
    ELSE IF @i % 5 = 2 SET @Ho = N'Lê';
    ELSE IF @i % 5 = 3 SET @Ho = N'Phạm';
    ELSE SET @Ho = N'Hoàng';

    -- Random Tên (Dựa theo chẵn lẻ để giả lập Nam/Nữ)
    IF @i % 2 = 0 
    BEGIN
        SET @TenDem = N'Văn';
        IF @i % 3 = 0 SET @Ten = N'Hùng';
        ELSE IF @i % 3 = 1 SET @Ten = N'Minh';
        ELSE SET @Ten = N'Đạt';
    END
    ELSE
    BEGIN
        SET @TenDem = N'Thị';
        IF @i % 3 = 0 SET @Ten = N'Lan';
        ELSE IF @i % 3 = 1 SET @Ten = N'Hương';
        ELSE SET @Ten = N'Mai';
    END

    SET @FullName = @Ho + ' ' + @TenDem + ' ' + @Ten;

    INSERT INTO dbo.Users (username, email, password, full_name) 
    VALUES (
        'user' + CAST(@i AS VARCHAR), 
        'user' + CAST(@i AS VARCHAR) + '@gmail.com', 
        '123456', 
        @FullName -- Tên thật: Nguyễn Văn Hùng, Trần Thị Lan...
    );
    SET @i = @i + 1;
END;
PRINT '--> Da tao xong 40 User ten that';
GO

-- =======================================================
-- BƯỚC 3: TẠO 20 ĐỀ THI (TÊN ĐỀ CHUYÊN NGÀNH)
-- =======================================================
DECLARE @i INT = 1;
DECLARE @ExamName NVARCHAR(100);
DECLARE @SubjectType INT; -- 1: Toán, 2: Lý, 3: Hóa

WHILE @i <= 20
BEGIN
    SET @SubjectType = (@i % 3) + 1; -- Xoay vòng 1, 2, 3

    IF @SubjectType = 1 SET @ExamName = N'Toán 12 - Kiểm tra Giải tích Chương ' + CAST(@i AS NVARCHAR);
    ELSE IF @SubjectType = 2 SET @ExamName = N'Vật Lý - Đề thi thử THPT Quốc Gia số ' + CAST(@i AS NVARCHAR);
    ELSE SET @ExamName = N'Hóa Học - Kiểm tra 15 phút Hữu cơ bài ' + CAST(@i AS NVARCHAR);

    INSERT INTO dbo.Exams (exam_name, created_by, created_date, test_time) 
    VALUES (
        @ExamName,
        'Admin',
        GETDATE(),
        15 -- 15 phút
    );
    SET @i = @i + 1;
END;
PRINT '--> Da tao xong 20 De thi ten that';
GO

-- =======================================================
-- BƯỚC 4: TẠO 600 CÂU HỎI (DỮ LIỆU THẬT TỪ NGÂN HÀNG ĐỀ)
-- =======================================================
-- Tạo bảng tạm chứa câu hỏi mẫu thật
DECLARE @Bank TABLE (
    SubjectType INT, -- 1: Toán, 2: Lý, 3: Hóa
    Content NVARCHAR(MAX), 
    OpA NVARCHAR(200), OpB NVARCHAR(200), OpC NVARCHAR(200), OpD NVARCHAR(200), 
    Ans CHAR(1)
);

-- NẠP DỮ LIỆU MẪU TOÁN (Type 1)
INSERT INTO @Bank VALUES 
(1, N'Nghiệm của phương trình 2x - 4 = 0 là?', N'x = 1', N'x = 2', N'x = 3', N'x = 4', 'B'),
(1, N'Đạo hàm của hàm số y = x^2 là?', N'2x', N'x', N'2', N'x^2', 'A'),
(1, N'Trong không gian Oxyz, vectơ đơn vị của trục Oy là?', N'(1;0;0)', N'(0;1;0)', N'(0;0;1)', N'(1;1;1)', 'B'),
(1, N'Diện tích hình tròn bán kính R = 3 là?', N'3π', N'6π', N'9π', N'12π', 'C'),
(1, N'Số phức z = 3 + 4i có phần thực là?', N'3', N'4', N'7', N'1', 'A'),
(1, N'Nguyên hàm của cos(x) là?', N'sin(x) + C', N'-sin(x) + C', N'cos(x) + C', N'tan(x) + C', 'A'),
(1, N'Hàm số nào sau đây đồng biến trên R?', N'y = x^3 + x', N'y = x^2', N'y = 1/x', N'y = -x', 'A');

-- NẠP DỮ LIỆU MẪU LÝ (Type 2)
INSERT INTO @Bank VALUES 
(2, N'Đơn vị đo cường độ dòng điện là?', N'Vôn (V)', N'Ampe (A)', N'Oát (W)', N'Jun (J)', 'B'),
(2, N'Công thức tính định luật Ohm là?', N'I = U.R', N'I = U/R', N'U = I/R', N'R = U.I', 'B'),
(2, N'Sóng âm không truyền được trong môi trường nào?', N'Rắn', N'Lỏng', N'Khí', N'Chân không', 'D'),
(2, N'Gia tốc trọng trường tại mặt đất xấp xỉ bằng?', N'9.8 m/s^2', N'100 m/s^2', N'3.14 m/s^2', N'0 m/s^2', 'A'),
(2, N'Hạt nhân nguyên tử gồm?', N'Proton và Electron', N'Proton và Neutron', N'Electron và Neutron', N'Chỉ Proton', 'B'),
(2, N'Ánh sáng đơn sắc đỏ có bước sóng khoảng?', N'0.38 µm', N'0.76 µm', N'0.55 µm', N'0.40 µm', 'B'),
(2, N'Máy biến áp dùng để làm gì?', N'Đổi tần số dòng điện', N'Đổi điện áp xoay chiều', N'Đổi dòng điện thành một chiều', N'Tạo ra điện năng', 'B');

-- NẠP DỮ LIỆU MẪU HÓA (Type 3)
INSERT INTO @Bank VALUES 
(3, N'Công thức hóa học của Axit Sulfuric là?', N'HCl', N'H2SO4', N'HNO3', N'NaCl', 'B'),
(3, N'Kim loại nào sau đây dẫn điện tốt nhất?', N'Nhôm', N'Sắt', N'Vàng', N'Bạc', 'D'),
(3, N'Dung dịch làm quỳ tím hóa đỏ là?', N'NaOH', N'Ca(OH)2', N'HCl', N'NaCl', 'C'),
(3, N'Khí metan có công thức là?', N'CH4', N'C2H4', N'C2H2', N'C6H6', 'A'),
(3, N'Số Avogadro có giá trị là?', N'6,023.10^23', N'3,14', N'9,8', N'1,6.10^-19', 'A'),
(3, N'Sắt (Fe) có hóa trị phổ biến là?', N'I và II', N'II và III', N'I và III', N'Chỉ II', 'B'),
(3, N'Chất nào dùng để sản xuất xà phòng?', N'Chất béo + Kiềm', N'Axit + Rượu', N'Andehit + H2', N'Glucozo', 'A');

-- SCRIPT CHÍNH: DUYỆT QUA ĐỀ THI VÀ LẤY CÂU HỎI TƯƠNG ỨNG TỪ BANK
DECLARE @examId INT = 1;
DECLARE @q INT = 1;
DECLARE @SubjType INT;

WHILE @examId <= 20
BEGIN
    SET @SubjType = (@examId % 3) + 1; -- Xác định đề này là môn gì (1,2,3)
    SET @q = 1;

    WHILE @q <= 30
    BEGIN
        -- Lấy ngẫu nhiên 1 câu hỏi từ BANK đúng môn đó
        INSERT INTO dbo.Question (exam_id, content, option_a, option_b, option_c, option_d, answer)
        SELECT TOP 1 
            @examId, Content, OpA, OpB, OpC, OpD, Ans
        FROM @Bank
        WHERE SubjectType = @SubjType
        ORDER BY NEWID(); -- Random trong Bank

        SET @q = @q + 1;
    END
    SET @examId = @examId + 1;
END;
PRINT '--> Da tao xong 600 Cau hoi kien thuc that';
GO

-- =======================================================
-- BƯỚC 5: TẠO 30 KẾT QUẢ THI
-- =======================================================
DECLARE @i INT = 1;
DECLARE @randStu INT;
DECLARE @randExam INT;
DECLARE @randScore FLOAT;

WHILE @i <= 30
BEGIN
    SELECT @randStu = FLOOR(RAND()*(40-1+1)+1);
    SELECT @randExam = FLOOR(RAND()*(20-1+1)+1);
    SELECT @randScore = CAST(FLOOR(RAND()*(100-0+1)) AS FLOAT) / 10;

    INSERT INTO dbo.Results (student_id, exam_id, score, submitted_date) 
    VALUES (
        @randStu,
        @randExam,
        @randScore,
        DATEADD(day, -@i, GETDATE())
    );
    SET @i = @i + 1;
END;
PRINT '--> HOAN TAT TOAN BO!';
GO