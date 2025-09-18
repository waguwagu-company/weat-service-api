-- DDL for WEAT Database

-- ============================================================
-- EXTENSIONS
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================
-- TABLES
-- ============================================================

-- 그룹
DROP TABLE IF EXISTS public."group" CASCADE;
CREATE TABLE public."group" (
                                group_id bpchar(32) NOT NULL DEFAULT replace(gen_random_uuid()::text, '-' , ''), -- 그룹식별자
                                is_single_member_group boolean NOT NULL DEFAULT false, -- 단일멤버그룹여부
                                created_at timestamptz NOT NULL DEFAULT now(), -- 생성일
                                updated_at timestamptz NOT NULL DEFAULT now(), -- 갱신일
                                CONSTRAINT group_pkey PRIMARY KEY (group_id)
);
COMMENT ON TABLE public."group" IS '그룹';
COMMENT ON COLUMN public."group".group_id IS '그룹식별자';
COMMENT ON COLUMN public."group".created_at IS '생성일';
COMMENT ON COLUMN public."group".updated_at IS '갱신일';


-- 파이프라인
DROP TABLE IF EXISTS public.pipeline CASCADE;
CREATE TABLE public.pipeline (
                                 pipeline_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,  -- 파이프라인식별자
                                 pipeline_name varchar(100),                                    -- 파이프라인명
                                 pipeline_description text,                                     -- 파이프라인설명
                                 created_at timestamptz NOT NULL DEFAULT now(),                 -- 생성일
                                 updated_at timestamptz NOT NULL DEFAULT now()                  -- 갱신일
);
COMMENT ON TABLE public.pipeline IS '파이프라인';
COMMENT ON COLUMN public.pipeline.pipeline_id IS '파이프라인식별자';
COMMENT ON COLUMN public.pipeline.pipeline_name IS '파이프라인명';
COMMENT ON COLUMN public.pipeline.pipeline_description IS '파이프라인설명';
COMMENT ON COLUMN public.pipeline.created_at IS '생성일';
COMMENT ON COLUMN public.pipeline.updated_at IS '갱신일';


-- 장소
DROP TABLE IF EXISTS public.place CASCADE;
CREATE TABLE public.place (
                              place_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,     -- 장소식별자
                              place_name varchar(255),                                       -- 장소명
                              place_roadname_address varchar(255),                           -- 장소도로명주소
                              place_url text,                                                -- 장소URL
                              created_at timestamptz NOT NULL DEFAULT now(),                 -- 생성일
                              updated_at timestamptz NOT NULL DEFAULT now()                  -- 갱신일
);
COMMENT ON TABLE public.place IS '장소';
COMMENT ON COLUMN public.place.place_id IS '장소식별자';
COMMENT ON COLUMN public.place.place_name IS '장소명';
COMMENT ON COLUMN public.place.place_roadname_address IS '장소도로명주소';
COMMENT ON COLUMN public.place.place_url IS '장소URL';
COMMENT ON COLUMN public.place.created_at IS '생성일';
COMMENT ON COLUMN public.place.updated_at IS '갱신일';


-- 서비스로그레벨
DROP TABLE IF EXISTS public.service_log_level CASCADE;
CREATE TABLE public.service_log_level (
                                          log_level_name varchar(50) PRIMARY KEY,                        -- 로그레벨명
                                          log_level_priority int4 NOT NULL,                              -- 로그레벨우선순위
                                          created_at timestamptz NOT NULL DEFAULT now(),                 -- 생성일
                                          updated_at timestamptz NOT NULL DEFAULT now()                  -- 갱신일
);
COMMENT ON TABLE public.service_log_level IS '서비스로그레벨';
COMMENT ON COLUMN public.service_log_level.log_level_name IS '로그레벨명';
COMMENT ON COLUMN public.service_log_level.log_level_priority IS '로그레벨우선순위';
COMMENT ON COLUMN public.service_log_level.created_at IS '생성일';
COMMENT ON COLUMN public.service_log_level.updated_at IS '갱신일';


-- 카테고리
DROP TABLE IF EXISTS public.category CASCADE;
CREATE TABLE public.category (
                                 category_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,   -- 카테고리식별자
                                 category_name varchar(50) NOT NULL,                             -- 카테고리명
                                 category_order int4 NOT NULL,                                   -- 카테고리순서
                                 category_parent_id int4 REFERENCES public.category(category_id),-- 카테고리부모식별자
                                 category_depth int4 NOT NULL DEFAULT 0,                         -- 카테고리깊이
                                 category_version varchar(10) NOT NULL DEFAULT 'v2',             -- 카테고리버전
                                 created_at timestamptz NOT NULL DEFAULT now(),                  -- 생성일
                                 updated_at timestamptz NOT NULL DEFAULT now()                   -- 갱신일
);
COMMENT ON TABLE public.category IS '카테고리';
COMMENT ON COLUMN public.category.category_id IS '카테고리식별자';
COMMENT ON COLUMN public.category.category_name IS '카테고리명';
COMMENT ON COLUMN public.category.category_order IS '카테고리순서';
COMMENT ON COLUMN public.category.category_parent_id IS '카테고리부모식별자';
COMMENT ON COLUMN public.category.category_depth IS '카테고리깊이';
COMMENT ON COLUMN public.category.category_version IS '카테고리버전';
COMMENT ON COLUMN public.category.created_at IS '생성일';
COMMENT ON COLUMN public.category.updated_at IS '갱신일';

CREATE INDEX idx_category_depth      ON public.category(category_depth);
CREATE INDEX idx_category_parent_id  ON public.category(category_parent_id);


-- 멤버
DROP TABLE IF EXISTS public."member" CASCADE;
CREATE TABLE public."member" (
                                 member_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,       -- 멤버식별자
                                 group_id bpchar(32) NOT NULL REFERENCES public."group"(group_id), -- 그룹식별자
                                 is_group_owner boolean NOT NULL DEFAULT false,                    -- 그룹오너여부
                                 created_at timestamptz NOT NULL DEFAULT now(),                    -- 생성일
                                 updated_at timestamptz NOT NULL DEFAULT now()                     -- 갱신일
);
COMMENT ON TABLE public."member" IS '멤버';
COMMENT ON COLUMN public."member".member_id IS '멤버식별자';
COMMENT ON COLUMN public."member".group_id IS '그룹식별자';
COMMENT ON COLUMN public."member".is_group_owner IS '그룹오너여부';
COMMENT ON COLUMN public."member".created_at IS '생성일';
COMMENT ON COLUMN public."member".updated_at IS '갱신일';

CREATE INDEX idx_member__group_id ON public."member"(group_id);


-- 분석
DROP TABLE IF EXISTS public.analysis CASCADE;
CREATE TABLE public.analysis (
                                 analysis_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,     -- 분석식별자
                                 group_id bpchar(32) NOT NULL REFERENCES public."group"(group_id), -- 그룹식별자
                                 analysis_status varchar(50),                                      -- 분석진행상황
                                 created_at timestamptz NOT NULL DEFAULT now(),                    -- 생성일
                                 updated_at timestamptz NOT NULL DEFAULT now()                     -- 갱신일
);
COMMENT ON TABLE public.analysis IS '분석';
COMMENT ON COLUMN public.analysis.analysis_id IS '분석식별자';
COMMENT ON COLUMN public.analysis.group_id IS '그룹식별자';
COMMENT ON COLUMN public.analysis.analysis_status IS '분석진행상황';
COMMENT ON COLUMN public.analysis.created_at IS '생성일';
COMMENT ON COLUMN public.analysis.updated_at IS '갱신일';

CREATE INDEX idx_analysis__group_id ON public.analysis(group_id);


-- 분석결과
DROP TABLE IF EXISTS public.analysis_result CASCADE;
CREATE TABLE public.analysis_result (
                                        analysis_result_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 분석결과식별자
                                        group_id bpchar(32) NOT NULL REFERENCES public."group"(group_id),    -- 그룹식별자
                                        analysis_id int4 NOT NULL REFERENCES public.analysis(analysis_id),   -- 분석식별자
                                        created_at timestamptz NOT NULL DEFAULT now(),                       -- 생성일
                                        updated_at timestamptz NOT NULL DEFAULT now()                        -- 갱신일
);
COMMENT ON TABLE public.analysis_result IS '분석결과';
COMMENT ON COLUMN public.analysis_result.analysis_result_id IS '분석결과식별자';
COMMENT ON COLUMN public.analysis_result.group_id IS '그룹식별자';
COMMENT ON COLUMN public.analysis_result.analysis_id IS '분석식별자';
COMMENT ON COLUMN public.analysis_result.created_at IS '생성일';
COMMENT ON COLUMN public.analysis_result.updated_at IS '갱신일';

CREATE INDEX idx_analysis_result__group_id    ON public.analysis_result(group_id);
CREATE INDEX idx_analysis_result__analysis_id ON public.analysis_result(analysis_id);


--- 분석결과상세
DROP TABLE IF EXISTS public.analysis_result_detail CASCADE;
CREATE TABLE public.analysis_result_detail (
                                               analysis_result_detail_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,             -- 상세식별자
                                               analysis_result_id int4 NOT NULL REFERENCES public.analysis_result(analysis_result_id), -- 분석결과식별자
                                               place_id int4 NOT NULL REFERENCES public.place(place_id),                               -- 장소식별자
                                               analysis_result_detail_content text,                                                    -- 분석결과상세내용
                                               analysis_result_detail_template_message text,                                           -- 분석결과상세템플릿메시지
                                               analysis_result_keywords jsonb NOT NULL DEFAULT '[]'::jsonb,                            -- 분석결과키워드
                                               created_at timestamptz NOT NULL DEFAULT now(),                                          -- 생성일
                                               updated_at timestamptz NOT NULL DEFAULT now()                                           -- 갱신일
);
COMMENT ON TABLE public.analysis_result_detail IS '분석결과상세';
COMMENT ON COLUMN public.analysis_result_detail.analysis_result_detail_id IS '분석결과상세식별자';
COMMENT ON COLUMN public.analysis_result_detail.analysis_result_id IS '분석결과식별자';
COMMENT ON COLUMN public.analysis_result_detail.place_id IS '장소식별자';
COMMENT ON COLUMN public.analysis_result_detail.analysis_result_detail_content IS '분석근거들을 조합하여 하나로 취합된 결과';
COMMENT ON COLUMN public.analysis_result_detail.analysis_result_detail_template_message IS '분석결과상세템플릿메시지';
COMMENT ON COLUMN public.analysis_result_detail.analysis_result_keywords IS '분석결과키워드';
COMMENT ON COLUMN public.analysis_result_detail.created_at IS '생성일';
COMMENT ON COLUMN public.analysis_result_detail.updated_at IS '갱신일';

CREATE INDEX idx_analysis_result_detail__analysis_result_id ON public.analysis_result_detail(analysis_result_id);
CREATE INDEX idx_analysis_result_detail__place_id           ON public.analysis_result_detail(place_id);


-- 장소이미지
DROP TABLE IF EXISTS public.place_image CASCADE;
CREATE TABLE public.place_image (
                                    place_image_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 장소이미지식별자
                                    place_id int4 NOT NULL REFERENCES public.place(place_id),        -- 장소식별자
                                    place_image_url text,                                            -- 이미지주소
                                    created_at timestamptz NOT NULL DEFAULT now(),                   -- 생성일
                                    updated_at timestamptz NOT NULL DEFAULT now()                    -- 갱신일
);
COMMENT ON TABLE public.place_image IS '장소이미지';
COMMENT ON COLUMN public.place_image.place_image_id IS '장소이미지식별자';
COMMENT ON COLUMN public.place_image.place_id IS '장소식별자';
COMMENT ON COLUMN public.place_image.place_image_url IS '장소이미지주소';
COMMENT ON COLUMN public.place_image.created_at IS '생성일';
COMMENT ON COLUMN public.place_image.updated_at IS '갱신일';

CREATE INDEX idx_place_image__place_id ON public.place_image(place_id);


-- 분석근거
DROP TABLE IF EXISTS public.analysis_basis CASCADE;
CREATE TABLE public.analysis_basis (
                                       analysis_basis_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 분석근거식별자
                                       analysis_result_detail_id int4 NOT NULL                             -- 분석근거상세식별자
                                           REFERENCES public.analysis_result_detail(analysis_result_detail_id),
                                       analysis_basis_type varchar(50),                                    -- 리뷰/AI추천
                                       analysis_basis_content text,                                        -- 분석근거내용
                                       analysis_score int4 NOT NULL DEFAULT 0,                             -- 분석점수
                                       created_at timestamptz NOT NULL DEFAULT now(),                      -- 생성일
                                       updated_at timestamptz NOT NULL DEFAULT now()                       -- 갱신일
);
COMMENT ON TABLE public.analysis_basis IS '분석근거';
COMMENT ON COLUMN public.analysis_basis.analysis_basis_id IS '분석근거식별자';
COMMENT ON COLUMN public.analysis_basis.analysis_result_detail_id IS '분석근거상세식별자';
COMMENT ON COLUMN public.analysis_basis.analysis_basis_type IS '리뷰/AI추천';
COMMENT ON COLUMN public.analysis_basis.analysis_basis_content IS '분석근거내용';
COMMENT ON COLUMN public.analysis_basis.analysis_score IS '분석점수';
COMMENT ON COLUMN public.analysis_basis.created_at IS '생성일';
COMMENT ON COLUMN public.analysis_basis.updated_at IS '갱신일';


-- 분석결과좋아요
DROP TABLE IF EXISTS public.analysis_result_like CASCADE;
CREATE TABLE public.analysis_result_like (
                                             analysis_result_like_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,  -- 좋아요식별자
                                             analysis_result_detail_id int4 NOT NULL                                    -- 분석결과상세식별자
                                                 REFERENCES public.analysis_result_detail(analysis_result_detail_id),
                                             member_id int4 NOT NULL                                                    -- 멤버식별자
                                                 REFERENCES public."member"(member_id),
                                             created_at timestamptz NOT NULL DEFAULT now(),                             -- 생성일
                                             updated_at timestamptz NOT NULL DEFAULT now()                              -- 갱신일
);
COMMENT ON TABLE public.analysis_result_like IS '분석결과좋아요';
COMMENT ON COLUMN public.analysis_result_like.analysis_result_like_id IS '분석결과식별자';
COMMENT ON COLUMN public.analysis_result_like.analysis_result_detail_id IS '분석결과상세식별자';
COMMENT ON COLUMN public.analysis_result_like.member_id IS '멤버식별자';
COMMENT ON COLUMN public.analysis_result_like.created_at IS '생성일';
COMMENT ON COLUMN public.analysis_result_like.updated_at IS '갱신일';

CREATE INDEX idx_analysis_result_like__analysis_result_detail_id ON public.analysis_result_like(analysis_result_detail_id);
CREATE INDEX idx_analysis_result_like__member_id                ON public.analysis_result_like(member_id);


-- 분석설정
DROP TABLE IF EXISTS public.analysis_setting CASCADE;
CREATE TABLE public.analysis_setting (
                                         analysis_setting_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 분석설정식별자
                                         analysis_id int4 NOT NULL REFERENCES public.analysis(analysis_id),    -- 분석식별자
                                         member_id int4 NOT NULL REFERENCES public."member"(member_id),        -- 멤버식별자
                                         created_at timestamptz NOT NULL DEFAULT now(),                        -- 생성일
                                         updated_at timestamptz NOT NULL DEFAULT now()                         -- 갱신일
);
COMMENT ON TABLE public.analysis_setting IS '분석설정';
COMMENT ON COLUMN public.analysis_setting.analysis_setting_id IS '분석설정식별자';
COMMENT ON COLUMN public.analysis_setting.analysis_id IS '분석식별자';
COMMENT ON COLUMN public.analysis_setting.member_id IS '멤버식별자';
COMMENT ON COLUMN public.analysis_setting.created_at IS '생성일';
COMMENT ON COLUMN public.analysis_setting.updated_at IS '갱신일';

CREATE INDEX idx_analysis_setting__analysis_id ON public.analysis_setting(analysis_id);
CREATE INDEX idx_analysis_setting__member_id   ON public.analysis_setting(member_id);



-- 분석설정상세
DROP TABLE IF EXISTS public.analysis_setting_detail CASCADE;
CREATE TABLE public.analysis_setting_detail (
                                                analysis_setting_detail_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 분석설정상세식별자
                                                analysis_setting_id int4 NOT NULL REFERENCES public.analysis_setting(analysis_setting_id), -- 분석설정식별자
                                                dtype varchar,                                                                -- 데이터타입(구분)
                                                created_at timestamptz NOT NULL DEFAULT now(),                                -- 생성일
                                                updated_at timestamptz NOT NULL DEFAULT now()                                 -- 갱신일
);
COMMENT ON TABLE public.analysis_setting_detail IS '분석설정상세';
COMMENT ON COLUMN public.analysis_setting_detail.analysis_setting_detail_id IS '분석설정상세식별자';
COMMENT ON COLUMN public.analysis_setting_detail.analysis_setting_id IS '분석설정식별자';
COMMENT ON COLUMN public.analysis_setting_detail.dtype IS '데이터타입';
COMMENT ON COLUMN public.analysis_setting_detail.created_at IS '생성일';
COMMENT ON COLUMN public.analysis_setting_detail.updated_at IS '갱신일';

CREATE INDEX idx_analysis_setting_detail__analysis_setting_id ON public.analysis_setting_detail(analysis_setting_id);



-- 카테고리태그
DROP TABLE IF EXISTS public.category_tag CASCADE;
CREATE TABLE public.category_tag (
                                     category_tag_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 카테고리태그식별자
                                     category_id int4 NOT NULL                                         -- 카테고리식별자
                                         REFERENCES public.category(category_id),
                                     category_tag_name varchar(50) NOT NULL,                           -- 카테고리태그명
                                     category_tag_order int4 NOT NULL,                                 -- 카테고리태그순서
                                     category_tag_version varchar NOT NULL,                            -- 카테고리태그버전
                                     created_at timestamptz NOT NULL DEFAULT now(),                    -- 생성일
                                     updated_at timestamptz NOT NULL DEFAULT now()                     -- 갱신일
);
COMMENT ON TABLE public.category_tag IS '카테고리태그';
COMMENT ON COLUMN public.category_tag.category_tag_id IS '카테고리태그식별자';
COMMENT ON COLUMN public.category_tag.category_id IS '카테고리식별자';
COMMENT ON COLUMN public.category_tag.category_tag_name IS '카테고리태그명';
COMMENT ON COLUMN public.category_tag.category_tag_order IS '카테고리태그순서';
COMMENT ON COLUMN public.category_tag.category_tag_version IS '카테고리태그버전';
COMMENT ON COLUMN public.category_tag.created_at IS '생성일';
COMMENT ON COLUMN public.category_tag.updated_at IS '갱신일';

CREATE INDEX idx_category_tag__category_id ON public.category_tag(category_id);


-- 분석설정상세 - 카테고리설정
DROP TABLE IF EXISTS public.category_setting CASCADE;
CREATE TABLE public.category_setting (
                                         analysis_setting_detail_id int4 PRIMARY KEY REFERENCES public.analysis_setting_detail(analysis_setting_detail_id), -- 분석설정상세식별자
                                         category_id int4 NOT NULL REFERENCES public.category(category_id), -- 카테고리식별자
                                         category_tag_id int4 REFERENCES public.category_tag(category_tag_id), -- 카테고리태그식별자
                                         is_preferred boolean,                                           -- 호불호여부
                                         created_at timestamptz NOT NULL DEFAULT now(),                  -- 생성일
                                         updated_at timestamptz NOT NULL DEFAULT now()                   -- 갱신일
);
COMMENT ON TABLE public.category_setting IS '분석설정상세-카테고리설정';
COMMENT ON COLUMN public.category_setting.analysis_setting_detail_id IS '분석설정상세식별자';
COMMENT ON COLUMN public.category_setting.category_id IS '카테고리식별자';
COMMENT ON COLUMN public.category_setting.category_tag_id IS '카테고리태그식별자';
COMMENT ON COLUMN public.category_setting.is_preferred IS '호불호여부';
COMMENT ON COLUMN public.category_setting.created_at IS '생성일';
COMMENT ON COLUMN public.category_setting.updated_at IS '갱신일';

CREATE INDEX idx_category_setting__category_id     ON public.category_setting(category_id);
CREATE INDEX idx_category_setting__category_tag_id ON public.category_setting(category_tag_id);


-- 분석설정상세 - 위치설정
DROP TABLE IF EXISTS public.location_setting CASCADE;
CREATE TABLE public.location_setting (
                                         analysis_setting_detail_id int4 PRIMARY KEY REFERENCES public.analysis_setting_detail(analysis_setting_detail_id),
                                         x_position float8,                                              -- X좌표
                                         y_position float8,                                              -- Y좌표
                                         roadname_address varchar(100),                                  -- 도로명주소
                                         created_at timestamptz NOT NULL DEFAULT now(),                  -- 생성일
                                         updated_at timestamptz NOT NULL DEFAULT now()                   -- 갱신일
);
COMMENT ON TABLE public.location_setting IS '분석설정상세-위치설정';
COMMENT ON COLUMN public.location_setting.analysis_setting_detail_id IS '분석설정상세식별자';
COMMENT ON COLUMN public.location_setting.x_position IS 'X좌표';
COMMENT ON COLUMN public.location_setting.y_position IS 'Y좌표';
COMMENT ON COLUMN public.location_setting.roadname_address IS '도로명주소';
COMMENT ON COLUMN public.location_setting.created_at IS '생성일';
COMMENT ON COLUMN public.location_setting.updated_at IS '갱신일';



-- 분석설정상세 - 비정형입력설정
DROP TABLE IF EXISTS public.text_input_setting CASCADE;
CREATE TABLE public.text_input_setting (
                                           analysis_setting_detail_id int4 PRIMARY KEY
                                               REFERENCES public.analysis_setting_detail(analysis_setting_detail_id),
                                           input_text text,                                                -- 입력문장
                                           created_at timestamptz NOT NULL DEFAULT now(),                  -- 생성일
                                           updated_at timestamptz NOT NULL DEFAULT now()                   -- 갱신일
);
COMMENT ON TABLE public.text_input_setting IS '분석설정상세-비정형입력설정';
COMMENT ON COLUMN public.text_input_setting.analysis_setting_detail_id IS '분석설정상세식별자';
COMMENT ON COLUMN public.text_input_setting.input_text IS '입력문장';
COMMENT ON COLUMN public.text_input_setting.created_at IS '생성일';
COMMENT ON COLUMN public.text_input_setting.updated_at IS '갱신일';



-- 파이프라인 작업
DROP TABLE IF EXISTS public.pipeline_job CASCADE;
CREATE TABLE public.pipeline_job (
                                     pipeline_job_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 파이프라인작업식별자
                                     pipeline_id int4 NOT NULL                                         -- 파이프라인식별자
                                         REFERENCES public.pipeline(pipeline_id),
                                     pipeline_job_name varchar(50),                                    -- 파이프라인작업명
                                     pipeline_job_description text,                                    -- 파이프라인작업설명
                                     pipeline_job_order int4,                                          -- 파이프라인작업순서
                                     created_at timestamptz NOT NULL DEFAULT now(),                    -- 생성일
                                     updated_at timestamptz NOT NULL DEFAULT now()                     -- 갱신일
);
COMMENT ON TABLE public.pipeline_job IS '파이프라인작업';
COMMENT ON COLUMN public.pipeline_job.pipeline_job_id IS '파이프라인작업식별자';
COMMENT ON COLUMN public.pipeline_job.pipeline_id IS '파이프라인식별자';
COMMENT ON COLUMN public.pipeline_job.pipeline_job_name IS '파이프라인작업명';
COMMENT ON COLUMN public.pipeline_job.pipeline_job_description IS '파이프라인작업설명';
COMMENT ON COLUMN public.pipeline_job.pipeline_job_order IS '파이프라인작업순서';
COMMENT ON COLUMN public.pipeline_job.created_at IS '생성일';
COMMENT ON COLUMN public.pipeline_job.updated_at IS '갱신일';

CREATE INDEX idx_pipeline_job__pipeline_id ON public.pipeline_job(pipeline_id);



-- 파이프라인실행
DROP TABLE IF EXISTS public.pipeline_execution CASCADE;
CREATE TABLE public.pipeline_execution (
                                           pipeline_execution_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,  -- 파이프라인실행식별자
                                           pipeline_id int4 NOT NULL REFERENCES public.pipeline(pipeline_id),       -- 파이프라인식별자
                                           analysis_id int4 NOT NULL REFERENCES public.analysis(analysis_id),       -- 분석식별자
                                           pipeline_execution_status varchar(50),                                   -- 진행상태
                                           pipeline_execution_stage int4,                                           -- 진행단계
                                           pipeline_execution_start_time timestamptz NOT NULL DEFAULT now(),        -- 시작시간
                                           pipeline_execution_end_time timestamptz,                                 -- 종료시간
                                           pipeline_execution_duration int4,                                        -- 소요시간(초)
                                           created_at timestamptz NOT NULL DEFAULT now(),                           -- 생성일
                                           updated_at timestamptz NOT NULL DEFAULT now()                            -- 갱신일
);
COMMENT ON TABLE public.pipeline_execution IS '파이프라인실행';
COMMENT ON COLUMN public.pipeline_execution.pipeline_execution_id IS '파이프라인실행식별자';
COMMENT ON COLUMN public.pipeline_execution.pipeline_id IS '파이프라인식별자';
COMMENT ON COLUMN public.pipeline_execution.analysis_id IS '분석식별자';
COMMENT ON COLUMN public.pipeline_execution.pipeline_execution_status IS '파이프라인진행상태';
COMMENT ON COLUMN public.pipeline_execution.pipeline_execution_stage IS '파이프라인진행단계';
COMMENT ON COLUMN public.pipeline_execution.pipeline_execution_start_time IS '파이프라인시작시간';
COMMENT ON COLUMN public.pipeline_execution.pipeline_execution_end_time IS '파이프라인종료시간';
COMMENT ON COLUMN public.pipeline_execution.pipeline_execution_duration IS '파이프라인소요시간';
COMMENT ON COLUMN public.pipeline_execution.created_at IS '생성일';
COMMENT ON COLUMN public.pipeline_execution.updated_at IS '갱신일';

CREATE INDEX idx_pipeline_execution__pipeline_id ON public.pipeline_execution(pipeline_id);
CREATE INDEX idx_pipeline_execution__analysis_id ON public.pipeline_execution(analysis_id);



-- 파이프라인작업실행
DROP TABLE IF EXISTS public.pipeline_job_execution CASCADE;
CREATE TABLE public.pipeline_job_execution (
                                               pipeline_job_execution_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,    -- 작업실행식별자
                                               pipeline_job_id int4 NOT NULL REFERENCES public.pipeline_job(pipeline_job_id), -- 파이프라인작업식별자
                                               analysis_id int4 NOT NULL REFERENCES public.analysis(analysis_id),             -- 분석식별자
                                               job_execution_status varchar(50),                                              -- 작업진행상태
                                               job_execution_start_time timestamptz NOT NULL DEFAULT now(),                   -- 작업시작시간
                                               job_execution_end_time timestamptz,                                            -- 작업종료시간
                                               job_execution_duration int4,                                                   -- 작업소요시간
                                               job_execution_request_data jsonb,                                              -- 요청데이터
                                               job_execution_result_data jsonb,                                               -- 결과데이터
                                               created_at timestamptz NOT NULL DEFAULT now(),                                 -- 생성일
                                               updated_at timestamptz NOT NULL DEFAULT now()                                  -- 갱신일
);
COMMENT ON TABLE public.pipeline_job_execution IS '파이프라인작업실행';
COMMENT ON COLUMN public.pipeline_job_execution.pipeline_job_execution_id IS '파이프라인작업실행식별자';
COMMENT ON COLUMN public.pipeline_job_execution.pipeline_job_id IS '파이프라인작업식별자';
COMMENT ON COLUMN public.pipeline_job_execution.analysis_id IS '분석식별자';
COMMENT ON COLUMN public.pipeline_job_execution.job_execution_status IS '작업진행상태';
COMMENT ON COLUMN public.pipeline_job_execution.job_execution_start_time IS '작업시작시간';
COMMENT ON COLUMN public.pipeline_job_execution.job_execution_end_time IS '작업종료시간';
COMMENT ON COLUMN public.pipeline_job_execution.job_execution_duration IS '작업소요시간';
COMMENT ON COLUMN public.pipeline_job_execution.job_execution_request_data IS '요청데이터';
COMMENT ON COLUMN public.pipeline_job_execution.job_execution_result_data IS '결과데이터';
COMMENT ON COLUMN public.pipeline_job_execution.created_at IS '생성일';
COMMENT ON COLUMN public.pipeline_job_execution.updated_at IS '갱신일';

CREATE INDEX idx_pipeline_job_execution__pipeline_job_id ON public.pipeline_job_execution(pipeline_job_id);
CREATE INDEX idx_pipeline_job_execution__analysis_id     ON public.pipeline_job_execution(analysis_id);



-- 서비스로그
DROP TABLE IF EXISTS public.service_log CASCADE;
CREATE TABLE public.service_log (
                                    service_log_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 서비스로그식별자
                                    service_name varchar(100),                                       -- 서비스명
                                    log_level varchar(50),                                           -- 로그레벨
                                    log_message text,                                                -- 로그메시지
                                    log_json jsonb,                                                  -- 로그데이터
                                    created_at timestamptz NOT NULL DEFAULT now(),                   -- 생성일
                                    updated_at timestamptz NOT NULL DEFAULT now()                    -- 갱신일
);
COMMENT ON TABLE public.service_log IS '서비스로그';
COMMENT ON COLUMN public.service_log.service_log_id IS '서비스로그식별자';
COMMENT ON COLUMN public.service_log.service_name IS '서비스명';
COMMENT ON COLUMN public.service_log.log_level IS '로그레벨';
COMMENT ON COLUMN public.service_log.log_message IS '로그메시지';
COMMENT ON COLUMN public.service_log.log_json IS '로그데이터';
COMMENT ON COLUMN public.service_log.created_at IS '생성일';
COMMENT ON COLUMN public.service_log.updated_at IS '갱신일';



-- ============================================================
-- Functions
-- ============================================================
-- DROP FUNCTION IF EXISTS public.get_category_tree(int4);
-- CREATE OR REPLACE FUNCTION public.get_category_tree(p_id integer DEFAULT NULL::integer)
--     RETURNS jsonb
--     LANGUAGE plpgsql
-- AS $function$
-- DECLARE
--     n RECORD;
--     tags     jsonb;
--     children jsonb;
-- BEGIN
--     IF p_id IS NULL THEN
--         RETURN COALESCE(
--                 (
--                     SELECT jsonb_build_object(
--                                    'categoryList',
--                                    COALESCE(
--                                            jsonb_agg(
--                                                    public.get_category_tree(c.category_id)
--                                                    ORDER BY c.category_order, c.category_id
--                                            ),
--                                            '[]'::jsonb
--                                    )
--                            )
--                     FROM public.category c
--                     WHERE c.category_version = 'v2'
--                       AND c.category_parent_id IS NULL
--                 ),
--                 jsonb_build_object('categoryList', '[]'::jsonb)
--                );
--     END IF;
--
--     SELECT c.category_id, c.category_name, c.category_order, c.category_depth
--     INTO n
--     FROM public.category c
--     WHERE c.category_id = p_id
--       AND c.category_version = 'v2';
--
--     IF NOT FOUND THEN
--         RETURN NULL;
--     END IF;
--
--     SELECT COALESCE(
--                    jsonb_agg(
--                            jsonb_build_object(
--                                    'categoryTagId',    ct.category_tag_id,
--                                    'categoryTagOrder', ct.category_tag_order,
--                                    'label',            ct.category_tag_name,
--                                    'status',           'default'
--                            )
--                            ORDER BY ct.category_tag_order, ct.category_tag_id
--                    ),
--                    '[]'::jsonb
--            )
--     INTO tags
--     FROM public.category_tag ct
--     WHERE ct.category_id = p_id
--       AND ct.category_tag_version = 'v2';
--
--     SELECT COALESCE(
--                    jsonb_agg(
--                            public.get_category_tree(c2.category_id)
--                            ORDER BY c2.category_order, c2.category_id
--                    ),
--                    '[]'::jsonb
--            )
--     INTO children
--     FROM public.category c2
--     WHERE c2.category_version = 'v2'
--       AND c2.category_parent_id IS NOT DISTINCT FROM p_id;
--
--     RETURN jsonb_build_object(
--             'title',         n.category_name,
--             'categoryId',    n.category_id,
--             'categoryOrder', n.category_order,
--             'tags',          tags,
--             'children',      children
--            );
-- END
-- $function$;
