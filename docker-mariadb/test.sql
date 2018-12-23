
CREATE TABLE IF NOT EXISTS `card_details` (
  `cardid` varchar(255) NOT NULL ,
  `cardNumber` varchar(255) DEFAULT NULL,
  `cardUserFname` varchar(255) DEFAULT NULL,
  `cardUserLname` varchar(255) DEFAULT NULL,
  `cardDateEtabishment` varchar(255) DEFAULT NULL,
  `cardDateEpiration` varchar(255) DEFAULT NULL,
  `cardLocationEtabishment` varchar(255) DEFAULT NULL,
  `cardImatriculation` varchar(255) DEFAULT NULL,
  `cardUserSex` varchar(255) DEFAULT NULL,
  `cardUserPhoto` varchar(255) DEFAULT NULL,
  `cardUserAdress` varchar(255) DEFAULT NULL,
  `cardUserPofession` varchar(255) DEFAULT NULL,
  `cardUserFatherName` varchar(255) DEFAULT NULL,
  `cardUserFatherBirthDate` varchar(255) DEFAULT NULL,
  `cardUserMatherName` varchar(255) DEFAULT NULL,
  `cardUserMatherBirthDate` varchar(255) DEFAULT NULL,
  `cardNocaisse` varchar(255) DEFAULT NULL,
  `cardUserBirthDate` varchar(255) DEFAULT NULL,
  `cardUserBrithPlace` varchar(255) DEFAULT NULL,
  `cardUserNationality` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cardid`)
) ;
