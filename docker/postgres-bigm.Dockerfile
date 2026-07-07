# 도서 검색용 pg_bigm 확장을 포함한 PostgreSQL 이미지 (공식 postgres 이미지 + 소스 빌드)
FROM postgres:18

ARG PG_BIGM_VERSION=1.2-20250903

RUN apt-get update \
 && apt-get install -y --no-install-recommends build-essential postgresql-server-dev-18 libicu-dev curl ca-certificates \
 && curl -fsSL "https://github.com/pgbigm/pg_bigm/archive/refs/tags/v${PG_BIGM_VERSION}.tar.gz" | tar -xz \
 && make -C "pg_bigm-${PG_BIGM_VERSION}" USE_PGXS=1 install \
 && rm -rf "pg_bigm-${PG_BIGM_VERSION}" \
 && apt-get purge -y build-essential postgresql-server-dev-18 libicu-dev curl \
 && apt-get autoremove -y \
 && rm -rf /var/lib/apt/lists/*

# pg_bigm.similarity_limit 등 커스텀 파라미터 인식을 위해 라이브러리 preload
CMD ["postgres", "-c", "shared_preload_libraries=pg_bigm"]
