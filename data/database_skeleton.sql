-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.3.15-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for cryogen_accounts
CREATE DATABASE IF NOT EXISTS `cryogen_accounts` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_accounts`;

-- Dumping structure for table cryogen_accounts.sessions
CREATE TABLE IF NOT EXISTS `sessions` (
  `username` varchar(12) NOT NULL,
  `sess_id` varchar(100) NOT NULL,
  `expiry` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_accounts.tokens
CREATE TABLE IF NOT EXISTS `tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(12) NOT NULL,
  `token` varchar(100) NOT NULL,
  `expiry` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_comments
CREATE DATABASE IF NOT EXISTS `cryogen_comments` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_comments`;

-- Dumping structure for table cryogen_comments.comments
CREATE TABLE IF NOT EXISTS `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `list_id` int(11) NOT NULL,
  `username` varchar(12) NOT NULL,
  `comment` varchar(1000) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_comments.lists
CREATE TABLE IF NOT EXISTS `lists` (
  `list_id` int(11) NOT NULL,
  `rights` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_discord
CREATE DATABASE IF NOT EXISTS `cryogen_discord` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_discord`;

-- Dumping structure for table cryogen_discord.discord_linked
CREATE TABLE IF NOT EXISTS `discord_linked` (
  `username` varchar(14) NOT NULL,
  `discord_id` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_discord.discord_verify
CREATE TABLE IF NOT EXISTS `discord_verify` (
  `username` varchar(14) NOT NULL,
  `rand_hash` varchar(50) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_display
CREATE DATABASE IF NOT EXISTS `cryogen_display` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_display`;

-- Dumping structure for table cryogen_display.current_names
CREATE TABLE IF NOT EXISTS `current_names` (
  `username` varchar(12) NOT NULL,
  `display_name` varchar(12) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_display.delays
CREATE TABLE IF NOT EXISTS `delays` (
  `username` varchar(12) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_display.last_names
CREATE TABLE IF NOT EXISTS `last_names` (
  `username` varchar(12) NOT NULL,
  `display_name` varchar(12) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_email
CREATE DATABASE IF NOT EXISTS `cryogen_email` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_email`;

-- Dumping structure for table cryogen_email.linked
CREATE TABLE IF NOT EXISTS `linked` (
  `username` varchar(12) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_email.temp
CREATE TABLE IF NOT EXISTS `temp` (
  `username` varchar(12) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `random` varchar(20) DEFAULT NULL,
  `expiry` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_forum
CREATE DATABASE IF NOT EXISTS `cryogen_forum` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_forum`;

-- Dumping structure for table cryogen_forum.categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(500) NOT NULL,
  `permissions` int(11) NOT NULL,
  `priority` int(11) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `added` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.forum_linked
CREATE TABLE IF NOT EXISTS `forum_linked` (
  `username` varchar(12) NOT NULL,
  `forum_id` int(10) NOT NULL,
  KEY `forum_id` (`forum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.forum_verify
CREATE TABLE IF NOT EXISTS `forum_verify` (
  `username` varchar(12) NOT NULL,
  `forum_id` int(10) NOT NULL,
  `pm_id` int(10) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.permissions
CREATE TABLE IF NOT EXISTS `permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `can_read_threads` varchar(500) NOT NULL DEFAULT '',
  `can_read_own_threads` varchar(500) NOT NULL DEFAULT '',
  `can_create_thread` varchar(500) NOT NULL DEFAULT '',
  `can_read_staff_threads` varchar(500) NOT NULL DEFAULT '',
  `can_create_polls` varchar(500) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.posts
CREATE TABLE IF NOT EXISTS `posts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `thread_id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  `post` varchar(50000) NOT NULL,
  `added` timestamp NOT NULL DEFAULT current_timestamp(),
  `edited` timestamp NULL DEFAULT current_timestamp(),
  `updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.subforums
CREATE TABLE IF NOT EXISTS `subforums` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(50) NOT NULL,
  `parent_id` int(11) NOT NULL,
  `parent_is_category` int(11) NOT NULL,
  `is_category` int(11) NOT NULL,
  `is_link` int(11) NOT NULL,
  `permissions` int(11) NOT NULL,
  `priority` int(11) NOT NULL,
  `link` varchar(50) NOT NULL DEFAULT '',
  `added` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.thanks
CREATE TABLE IF NOT EXISTS `thanks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `added` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.threads
CREATE TABLE IF NOT EXISTS `threads` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `forum_id` int(11) NOT NULL DEFAULT 0,
  `title` varchar(50) NOT NULL,
  `author` int(11) NOT NULL,
  `last_post_id` int(11) NOT NULL,
  `last_post_author` int(11) NOT NULL,
  `last_post_time` timestamp NULL DEFAULT NULL,
  `has_poll` int(11) NOT NULL,
  `poll_id` int(11) NOT NULL,
  `added` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_forum.usergroups
CREATE TABLE IF NOT EXISTS `usergroups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `rights` int(11) NOT NULL,
  `colour` varchar(50) DEFAULT NULL,
  `image_before` varchar(50) DEFAULT NULL,
  `image_after` varchar(50) DEFAULT NULL,
  `user_title` varchar(50) DEFAULT NULL,
  `added` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_global
CREATE DATABASE IF NOT EXISTS `cryogen_global` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_global`;

-- Dumping structure for table cryogen_global.alogs
CREATE TABLE IF NOT EXISTS `alogs` (
  `username` varchar(14) NOT NULL,
  `date` date NOT NULL,
  `event` int(3) NOT NULL,
  `npc_id` int(10) NOT NULL,
  `npc_kills` int(10) NOT NULL,
  `quest_id` int(10) NOT NULL,
  `drop_id` int(10) NOT NULL,
  `drop_amount` int(10) NOT NULL,
  `skill_id` int(2) NOT NULL,
  `level` int(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.announcements
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` int(100) NOT NULL AUTO_INCREMENT,
  `username` varchar(12) NOT NULL,
  `title` varchar(100) NOT NULL,
  `announcement` varchar(1000) NOT NULL,
  `read` varchar(1000) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `expiry` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.client_data
CREATE TABLE IF NOT EXISTS `client_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(50) NOT NULL,
  `last_update` varchar(1000) DEFAULT '',
  `latest` int(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.events
CREATE TABLE IF NOT EXISTS `events` (
  `event_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.highscores
CREATE TABLE IF NOT EXISTS `highscores` (
  `username` varchar(14) NOT NULL,
  `total_level` int(5) NOT NULL,
  `total_xp` double NOT NULL,
  `skill_0` int(20) NOT NULL,
  `skill_1` int(20) NOT NULL,
  `skill_2` int(20) NOT NULL,
  `skill_3` int(20) NOT NULL,
  `skill_4` int(20) NOT NULL,
  `skill_5` int(20) NOT NULL,
  `skill_6` int(20) NOT NULL,
  `skill_7` int(20) NOT NULL,
  `skill_8` int(20) NOT NULL,
  `skill_9` int(20) NOT NULL,
  `skill_10` int(20) NOT NULL,
  `skill_11` int(20) NOT NULL,
  `skill_12` int(20) NOT NULL,
  `skill_13` int(20) NOT NULL,
  `skill_14` int(20) NOT NULL,
  `skill_15` int(20) NOT NULL,
  `skill_16` int(20) NOT NULL,
  `skill_17` int(20) NOT NULL,
  `skill_18` int(20) NOT NULL,
  `skill_19` int(20) NOT NULL,
  `skill_20` int(20) NOT NULL,
  `skill_21` int(20) NOT NULL,
  `skill_22` int(20) NOT NULL,
  `skill_23` int(20) NOT NULL,
  `skill_24` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.misc_data
CREATE TABLE IF NOT EXISTS `misc_data` (
  `name` varchar(50) DEFAULT NULL,
  `value` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.personal_shop
CREATE TABLE IF NOT EXISTS `personal_shop` (
  `username` varchar(12) NOT NULL,
  `items` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.player_data
CREATE TABLE IF NOT EXISTS `player_data` (
  `id` int(100) NOT NULL AUTO_INCREMENT,
  `username` varchar(14) NOT NULL,
  `password` varchar(100) NOT NULL,
  `salt` varchar(100) NOT NULL,
  `rights` int(2) NOT NULL,
  `donator` int(2) NOT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `display_group` int(11) NOT NULL DEFAULT 2,
  `usergroups` varchar(500) DEFAULT NULL,
  `creation_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_global.weblogs
CREATE TABLE IF NOT EXISTS `weblogs` (
  `logMessage` varchar(50) DEFAULT NULL,
  `logType` varchar(50) DEFAULT NULL,
  `date` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_logs
CREATE DATABASE IF NOT EXISTS `cryogen_logs` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_logs`;

-- Dumping structure for table cryogen_logs.commands
CREATE TABLE IF NOT EXISTS `commands` (
  `username` varchar(12) NOT NULL,
  `command` varchar(50) NOT NULL,
  `parameters` varchar(100) NOT NULL,
  `ip` varchar(50) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.dice
CREATE TABLE IF NOT EXISTS `dice` (
  `winner` varchar(12) NOT NULL,
  `loser` varchar(12) NOT NULL,
  `winner_ip` varchar(100) NOT NULL,
  `loser_ip` varchar(100) NOT NULL,
  `winner_stake` varchar(1000) NOT NULL,
  `loser_stake` varchar(1000) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.drop
CREATE TABLE IF NOT EXISTS `drop` (
  `username` varchar(12) NOT NULL,
  `id` int(10) NOT NULL,
  `amount` int(20) NOT NULL,
  `ip` varchar(100) NOT NULL,
  `drop_type` int(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.duel
CREATE TABLE IF NOT EXISTS `duel` (
  `winner` varchar(12) NOT NULL,
  `loser` varchar(12) NOT NULL,
  `winning_stake` varchar(1000) NOT NULL,
  `losing_stake` varchar(1000) NOT NULL,
  `winning_ip` varchar(100) NOT NULL,
  `losing_ip` varchar(100) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.login
CREATE TABLE IF NOT EXISTS `login` (
  `username` varchar(12) NOT NULL,
  `ip` varchar(100) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.pickup
CREATE TABLE IF NOT EXISTS `pickup` (
  `username` varchar(12) NOT NULL,
  `id` int(20) NOT NULL,
  `amount` int(20) NOT NULL,
  `owner` varchar(12) NOT NULL,
  `tile` varchar(100) NOT NULL,
  `ip` varchar(100) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.pos
CREATE TABLE IF NOT EXISTS `pos` (
  `buyer` varchar(12) NOT NULL,
  `owner` varchar(12) NOT NULL,
  `id` int(20) NOT NULL,
  `amount` int(20) NOT NULL,
  `ip` varchar(50) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.punish
CREATE TABLE IF NOT EXISTS `punish` (
  `punishee` varchar(12) NOT NULL,
  `punisher` varchar(12) NOT NULL,
  `remover` varchar(12) NOT NULL,
  `type` int(2) NOT NULL,
  `ip` varchar(50) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.pvp
CREATE TABLE IF NOT EXISTS `pvp` (
  `winner` varchar(12) NOT NULL,
  `loser` varchar(12) NOT NULL,
  `items` varchar(1000) NOT NULL,
  `winner_ip` varchar(100) NOT NULL,
  `loser_ip` varchar(100) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.shop
CREATE TABLE IF NOT EXISTS `shop` (
  `username` varchar(12) NOT NULL,
  `id` int(20) NOT NULL,
  `amount` int(20) NOT NULL,
  `ip` varchar(50) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_logs.trade
CREATE TABLE IF NOT EXISTS `trade` (
  `trader` varchar(12) NOT NULL,
  `tradee` varchar(12) NOT NULL,
  `trader_items` varchar(1000) NOT NULL,
  `tradee_items` varchar(1000) NOT NULL,
  `trader_ip` varchar(100) NOT NULL,
  `tradee_ip` varchar(100) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_previous
CREATE DATABASE IF NOT EXISTS `cryogen_previous` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_previous`;

-- Dumping structure for table cryogen_previous.ips
CREATE TABLE IF NOT EXISTS `ips` (
  `username` varchar(12) NOT NULL,
  `value` varchar(10000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='previous ips. Username - { { ip, datelong }, ... }';

-- Data exporting was unselected.

-- Dumping structure for table cryogen_previous.passwords
CREATE TABLE IF NOT EXISTS `passwords` (
  `salt` varchar(100) NOT NULL,
  `hashes` varchar(10000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_punish
CREATE DATABASE IF NOT EXISTS `cryogen_punish` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_punish`;

-- Dumping structure for table cryogen_punish.appeals
CREATE TABLE IF NOT EXISTS `appeals` (
  `id` int(200) NOT NULL AUTO_INCREMENT,
  `type` int(1) NOT NULL DEFAULT 0,
  `punish_id` int(200) NOT NULL,
  `comment_list` int(200) NOT NULL,
  `username` varchar(12) NOT NULL,
  `title` varchar(20) NOT NULL,
  `message` varchar(1000) NOT NULL,
  `reason` varchar(1000) NOT NULL DEFAULT '',
  `action` varchar(1000) NOT NULL DEFAULT 'Created',
  `read` varchar(10000) NOT NULL DEFAULT '',
  `active` int(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `answered` timestamp NULL DEFAULT current_timestamp(),
  `answerer` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_punish.punishments
CREATE TABLE IF NOT EXISTS `punishments` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `appeal_id` int(20) NOT NULL DEFAULT 0,
  `username` varchar(12) NOT NULL,
  `type` int(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `expiry` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `punisher` varchar(12) NOT NULL,
  `reason` varchar(100) NOT NULL,
  `active` int(1) NOT NULL DEFAULT 1,
  `archived` timestamp NULL DEFAULT current_timestamp(),
  `archiver` varchar(12) DEFAULT NULL,
  `comment_list` int(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_recovery
CREATE DATABASE IF NOT EXISTS `cryogen_recovery` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_recovery`;

-- Dumping structure for table cryogen_recovery.instant
CREATE TABLE IF NOT EXISTS `instant` (
  `id` varchar(50) NOT NULL,
  `rand` varchar(50) NOT NULL,
  `method` int(1) NOT NULL,
  `status` int(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_recovery.recoveries
CREATE TABLE IF NOT EXISTS `recoveries` (
  `id` varchar(50) NOT NULL,
  `username` varchar(12) NOT NULL,
  `email` varchar(50) NOT NULL,
  `forum` int(11) NOT NULL,
  `creation` timestamp NULL DEFAULT NULL,
  `cico` varchar(50) NOT NULL,
  `additional` varchar(250) NOT NULL,
  `pass0` int(1) NOT NULL,
  `pass1` int(1) NOT NULL,
  `pass2` int(1) NOT NULL,
  `status` int(1) NOT NULL,
  `new_pass` varchar(500) NOT NULL,
  `reason` varchar(75) NOT NULL,
  `read` varchar(2000) NOT NULL,
  `ip` varchar(50) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_reports
CREATE DATABASE IF NOT EXISTS `cryogen_reports` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_reports`;

-- Dumping structure for table cryogen_reports.bug_reports
CREATE TABLE IF NOT EXISTS `bug_reports` (
  `id` int(200) NOT NULL AUTO_INCREMENT,
  `username` varchar(12) NOT NULL,
  `title` varchar(55) NOT NULL,
  `replicated` varchar(20) NOT NULL,
  `seen` varchar(55) NOT NULL,
  `info` varchar(1000) NOT NULL,
  `last_action` varchar(1000) NOT NULL DEFAULT 'Created',
  `comment_list` int(10) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `archived` timestamp NULL DEFAULT current_timestamp(),
  `archiver` varchar(12) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `type` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_reports.player_reports
CREATE TABLE IF NOT EXISTS `player_reports` (
  `id` int(200) NOT NULL AUTO_INCREMENT,
  `username` varchar(12) NOT NULL,
  `title` varchar(55) NOT NULL,
  `offender` varchar(12) NOT NULL,
  `rule` varchar(20) NOT NULL,
  `info` varchar(1000) NOT NULL,
  `proof` varchar(1000) NOT NULL,
  `last_action` varchar(1000) NOT NULL DEFAULT 'Created',
  `comment_list` int(10) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `archived` timestamp NULL DEFAULT current_timestamp(),
  `archiver` varchar(12) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `type` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_shop
CREATE DATABASE IF NOT EXISTS `cryogen_shop` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_shop`;

-- Dumping structure for table cryogen_shop.cart_data
CREATE TABLE IF NOT EXISTS `cart_data` (
  `username` varchar(12) NOT NULL,
  `items` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_shop.invoices
CREATE TABLE IF NOT EXISTS `invoices` (
  `id` varchar(50) NOT NULL,
  `username` varchar(12) NOT NULL,
  `items` varchar(1000) NOT NULL,
  `active` int(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_shop.item_data
CREATE TABLE IF NOT EXISTS `item_data` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `price` int(4) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `imageName` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `active` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_shop.packages
CREATE TABLE IF NOT EXISTS `packages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(12) NOT NULL DEFAULT '0',
  `package_id` int(10) NOT NULL DEFAULT 0,
  `invoice_id` varchar(1000) NOT NULL DEFAULT '0',
  `active` int(1) NOT NULL DEFAULT 0,
  `date` timestamp NOT NULL DEFAULT current_timestamp(),
  `redeem_date` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping database structure for cryogen_vote
CREATE DATABASE IF NOT EXISTS `cryogen_vote` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cryogen_vote`;

-- Dumping structure for table cryogen_vote.auths
CREATE TABLE IF NOT EXISTS `auths` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `auth` varchar(12) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_vote.auth_logs
CREATE TABLE IF NOT EXISTS `auth_logs` (
  `id` int(255) NOT NULL,
  `usernames` varchar(26) NOT NULL,
  `auths` varchar(26) NOT NULL,
  `details` varchar(26) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cryogen_vote.vote_data
CREATE TABLE IF NOT EXISTS `vote_data` (
  `username` varchar(12) NOT NULL,
  `runelocus` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `rune-server` timestamp NOT NULL DEFAULT current_timestamp(),
  `toplist` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
