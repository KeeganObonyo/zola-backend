INSERT INTO `user` (`username`, `email`, `password`,`first_name`,`last_name`,`business_name`,`apikey`) VALUES ('komodo','komodo@zola.reviews', SHA2('testpass1', 256),'Aligator','Lines','Kilimanjaro', SHA2('as78286901309113v462', 256));
INSERT INTO `user` (`username`, `email`, `password`,`first_name`,`last_name`,`business_name`,`apikey`) VALUES ('dragon','dragon@zola.reviews', SHA2('testpass2', 256),'Black','Mamba','Kilimanjaro', SHA2('w10388700305539094fq', 256));

-- as78286901309113v462
-- w10388700305539094fq

SHA2('as78286901309113v462', 256)
SHA2('w10388700305539094fq', 256)