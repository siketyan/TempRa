CREATE TABLE `records` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `day` tinyint(2) NOT NULL,
  `hour` tinyint(2) NOT NULL,
  `minute` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `hours` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `day` tinyint(2) NOT NULL,
  `hour` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `days` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `day` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `months` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `years` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;