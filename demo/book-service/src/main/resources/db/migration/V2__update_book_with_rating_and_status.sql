ALTER TABLE books
    ADD COLUMN rating DECIMAL(3,2) CHECK (rating >= 0 AND rating <= 5);

CREATE TYPE payment_status AS ENUM (
    'NO_PAYMENT',
    'PAYMENT_PENDING',
    'PAYMENT_SUCCEED'
    );
CREATE CAST (varchar AS payment_status) WITH INOUT AS IMPLICIT;

ALTER TABLE books
    ADD COLUMN status payment_status NOT NULL DEFAULT 'NO_PAYMENT';