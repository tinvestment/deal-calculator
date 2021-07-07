CREATE KEYSPACE IF NOT EXISTS investment
            WITH REPLICATION = {'class': 'NetworkTopologyStrategy', 'Mars' : 1};

USE investment;

CREATE TABLE IF NOT EXISTS deal
(
    id             bigint,
    account_number text,
    date_time      timestamp,
    operation_type text,
    currency       text,
    quantity       decimal,
    payment        decimal,
    PRIMARY KEY ((account_number), date_time)
)
            WITH CLUSTERING ORDER BY (date_time DESC) AND COMMENT = 'Client deal';

CREATE ROLE IF NOT EXISTS deal_calculator WITH LOGIN = true AND PASSWORD = 'deal_calculator';
GRANT SELECT ON investment.deal TO deal_calculator;
GRANT MODIFY ON investment.deal TO deal_calculator;
