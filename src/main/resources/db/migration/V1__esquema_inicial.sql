-- =====================================================================
-- V1: esquema inicial de PharmaBackend (Oracle)
--
-- Tipos elegidos para coincidir con lo que Hibernate 7 (OracleDialect)
-- genera a partir de las entidades, de modo que ddl-auto=validate pase:
--   Long          -> bigint
--   Integer       -> integer
--   Boolean       -> boolean            (Oracle 23ai; ver nota abajo)
--   BigDecimal    -> number(p,s)
--   LocalDateTime -> timestamp
--   String(n)     -> varchar(n char)
--
-- Nota: en Oracle 21c o anterior no existe el tipo boolean; en ese caso
-- reemplazar por number(1,0) con check (col in (0,1)).
-- =====================================================================

create table categorias (
    id                 bigint generated always as identity,
    nombre             varchar(50)  not null,
    descripcion        varchar(200),
    estado             boolean            not null,
    fecha_creacion     timestamp       not null,
    fecha_modificacion timestamp,
    constraint pk_categorias primary key (id)
);

create unique index ux_categorias_nombre on categorias (upper(nombre));

create table clientes (
    id                 bigint generated always as identity,
    dni                varchar(8)   not null,
    nombres            varchar(100) not null,
    apellidos          varchar(100) not null,
    email              varchar(150) not null,
    telefono           varchar(15),
    direccion          varchar(250),
    estado             boolean            not null,
    fecha_creacion     timestamp       not null,
    fecha_modificacion timestamp,
    constraint pk_clientes primary key (id),
    constraint ux_clientes_dni unique (dni)
);

create unique index ux_clientes_email on clientes (upper(email));

create table productos (
    id                 bigint generated always as identity,
    nombre             varchar(150) not null,
    precio             numeric(10,2)       not null,
    stock              integer       not null,
    estado             boolean            not null,
    categoria_id       bigint       not null,
    fecha_creacion     timestamp       not null,
    fecha_modificacion timestamp,
    constraint pk_productos primary key (id),
    constraint fk_productos_categoria
        foreign key (categoria_id) references categorias (id),
    constraint ck_productos_stock check (stock >= 0)
);

create unique index ux_productos_nombre on productos (upper(nombre));
create index ix_productos_categoria on productos (categoria_id);

create table ventas (
    id         bigint generated always as identity,
    fecha      timestamp      not null,
    total      numeric(12,2)      not null,
    estado     varchar(20) not null,
    cliente_id bigint      not null,
    constraint pk_ventas primary key (id),
    constraint fk_ventas_cliente
        foreign key (cliente_id) references clientes (id),
    constraint ck_ventas_estado
        check (estado in ('REGISTRADA', 'ANULADA'))
);

create index ix_ventas_cliente on ventas (cliente_id);
create index ix_ventas_fecha   on ventas (fecha);

create table detalle_ventas (
    id          bigint generated always as identity,
    venta_id    bigint not null,
    producto_id bigint not null,
    cantidad    integer not null,
    precio      numeric(10,2) not null,
    subtotal    numeric(12,2) not null,
    constraint pk_detalle_ventas primary key (id),
    constraint fk_detalle_ventas_venta
        foreign key (venta_id) references ventas (id),
    constraint fk_detalle_ventas_producto
        foreign key (producto_id) references productos (id),
    constraint ck_detalle_ventas_cantidad check (cantidad > 0)
);

create index ix_detalle_ventas_venta    on detalle_ventas (venta_id);
create index ix_detalle_ventas_producto on detalle_ventas (producto_id);
