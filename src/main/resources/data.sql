ALTER USER SA SET PASSWORD 'pass';
insert into tariffs(first,second,next,vip) values(1,2,1.5,false);
insert into tariffs(first,second,next,vip) values(0,2,1.2,true);
insert into vip(registration_plate) values('WAW123');
insert into currencies(code,rate) values('PLN',1);
