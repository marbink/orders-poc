CREATE TABLE "order" (
	order_uuid uuid NOT NULL,
	creation_date timestamp(6) NOT NULL,
	last_update_date timestamp(6) NOT NULL,
	customer_uuid uuid NOT NULL,
	description varchar(255) NULL,
	CONSTRAINT order_pkey PRIMARY KEY (order_uuid)
);


CREATE TABLE product (
	product_uuid uuid NOT NULL,
	creation_date timestamp(6) NOT NULL,
	last_update_date timestamp(6) NOT NULL,
	available_quantity int4 NULL,
	price numeric(38, 2) NULL,
	product_name varchar(255) NULL,
	CONSTRAINT product_pkey PRIMARY KEY (product_uuid)
);


CREATE TABLE order_product (
	creation_date timestamp(6) NOT NULL,
	last_update_date timestamp(6) NOT NULL,
	quantity int4 NOT NULL,
	order_uuid uuid NOT NULL,
	product_uuid uuid NOT NULL,
	CONSTRAINT order_product_pkey PRIMARY KEY (order_uuid, product_uuid),
	CONSTRAINT fk__order_product__product__product_uuid FOREIGN KEY (product_uuid) REFERENCES product(product_uuid),
	CONSTRAINT fk__order_product__order__order_uuid FOREIGN KEY (order_uuid) REFERENCES "order"(order_uuid)
);

INSERT INTO product
(product_uuid, creation_date, last_update_date, price, product_name, available_quantity)
VALUES('2aaf5758-cdab-4fae-9209-1b5f98939259'::uuid, now(), now(), 19.99, 'WIFI_EXTENDER', 3);

INSERT INTO product
(product_uuid, creation_date, last_update_date, price, product_name, available_quantity)
VALUES('3aaf5758-cdab-4fae-9209-1b5f98939259'::uuid, now(), now(), 69.99, 'ROUTER_WIFI6', 1);