-- =============================================
-- AI求职助手 - 数据库建表脚本
-- 数据库名: ai_job_helper
-- =============================================

CREATE DATABASE IF NOT EXISTS ai_job_helper
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE ai_job_helper;

-- 简历表
CREATE TABLE IF NOT EXISTS resume (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name    VARCHAR(255)  NOT NULL COMMENT '原始文件名',
    content      LONGTEXT      NOT NULL COMMENT 'PDF解析后的纯文本',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历表';

-- 分析记录表
CREATE TABLE IF NOT EXISTS analysis (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    resume_id           BIGINT        NOT NULL COMMENT '关联简历ID',
    target_position     VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '目标岗位名称',
    jd_content          LONGTEXT      NOT NULL COMMENT 'JD原始文本',
    match_score         INT           COMMENT '匹配分数 0-100',
    strengths           TEXT          COMMENT '优势列表(JSON数组)',
    weaknesses          TEXT          COMMENT '不足列表(JSON数组)',
    suggested_skills    TEXT          COMMENT '建议补充技能(JSON数组)',
    interview_questions TEXT          COMMENT '面试题(JSON数组,10道)',
    raw_response        LONGTEXT      COMMENT 'DeepSeek原始响应(备用)',
    create_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (resume_id) REFERENCES resume(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分析记录表';
