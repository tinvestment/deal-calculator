CREATE KEYSPACE IF NOT EXISTS investment
            WITH REPLICATION = {'class': 'NetworkTopologyStrategy', 'Mars' : 1};

USE investment;

CREATE TABLE IF NOT EXISTS deal
(
    id                    bigint,
    account_number        text,
    date_time             timestamp,
    operation_type        text,
    currency              text,
    quantity              decimal,
    payment               decimal,
    ticker                text,
    isin                  text,
    instrument_short_name text,
    status                text,
    PRIMARY KEY ((account_number), date_time)
) WITH CLUSTERING ORDER BY (date_time DESC) AND COMMENT = 'Client deal';

CREATE TABLE IF NOT EXISTS currency_handbook
(
    id            text,
    ru_name       text,
    eng_name      text,
    denomination  bigint,
    iso_num_code  text,
    iso_char_code text,
    parent_code   text,
    PRIMARY KEY ((iso_char_code), id)
) WITH CLUSTERING ORDER BY (id DESC) AND COMMENT = 'Currency handbook';

CREATE ROLE IF NOT EXISTS deal_calculator WITH LOGIN = true AND PASSWORD = 'deal_calculator';
GRANT SELECT ON investment.deal TO deal_calculator;
GRANT MODIFY ON investment.deal TO deal_calculator;
GRANT SELECT ON investment.currency_handbook TO deal_calculator;
GRANT MODIFY ON investment.currency_handbook TO deal_calculator;