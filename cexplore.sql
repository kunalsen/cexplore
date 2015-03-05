-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 04, 2015 at 10:13 PM
-- Server version: 5.5.40
-- PHP Version: 5.3.10-1ubuntu3.15

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `concept_explorer`
--

-- --------------------------------------------------------

--
-- Table structure for table `endpoint`
--

CREATE TABLE IF NOT EXISTS `endpoint` (
  `name` varchar(20) NOT NULL,
  `uri` varchar(200) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `endpoint`
--

INSERT INTO `endpoint` (`name`, `uri`) VALUES
('dbpedia-live', 'http://dbpedia-live.openlinksw.com/sparql/'),
('dbpedia', 'http://dbpedia.org/sparql/');

-- --------------------------------------------------------

--
-- Table structure for table `param`
--

CREATE TABLE IF NOT EXISTS `param` (
  `query_id` varchar(100) NOT NULL DEFAULT '',
  `query_seq` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `value` varchar(2048) NOT NULL,
  PRIMARY KEY (`query_id`,`query_seq`,`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `param`
--

INSERT INTO `param` (`query_id`, `query_seq`, `name`, `value`) VALUES
('dbpedia_query', 1, 'query', 'CONSTRUCT { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> ?uri } WHERE { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> ?uri . }'),
('dbpedia_query', 1, 'debug', 'on'),
('dbpedia_query', 1, 'timeout', ''),
('dbpedia_query', 1, 'format', 'application/rdf+xml'),
('dbpedia_query', 1, 'save', 'display'),
('dbpedia_query', 2, 'query', 'CONSTRUCT { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?uri } WHERE { { <http://dbpedia.org/resource/%TERM%_%28disambiguation%29> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?uri . } UNION { ?uri <http://dbpedia.org/ontology/wikiPageDisambiguates> <http://dbpedia.org/resource/%TERM%_%28disambiguation%29> . } UNION { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?uri . } UNION { ?uri <http://dbpedia.org/ontology/wikiPageDisambiguates> <http://dbpedia.org/resource/%TERM%> . }   UNION { ?udis <http://dbpedia.org/ontology/wikiPageDisambiguates> <http://dbpedia.org/resource/%TERM%>. ?udis <http://dbpedia.org/ontology/wikiPageDisambiguates> ?uri .} }'),
('dbpedia_query', 2, 'debug', 'on'),
('dbpedia_query', 2, 'timeout', ''),
('dbpedia_query', 2, 'format', 'application/rdf+xml'),
('dbpedia_query', 2, 'save', 'display'),
('dbpedia_query', 3, 'query', 'CONSTRUCT { <http://dbpedia.org/resource/%TERM%> rdf:type ?class } WHERE { { <http://dbpedia.org/resource/%TERM%> rdf:type ?class } UNION  { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> ?uri . ?uri rdf:type ?class. } }'),
('dbpedia_query', 3, 'debug', 'on'),
('dbpedia_query', 3, 'timeout', ''),
('dbpedia_query', 3, 'format', 'application/rdf+xml'),
('dbpedia_query', 3, 'save', 'display'),
('dbpedia_query', 4, 'query', 'CONSTRUCT { <http://dbpedia.org/resource/%TERM%> <http://purl.org/dc/terms/subject> ?topics } WHERE { { <http://dbpedia.org/resource/%TERM%> <http://purl.org/dc/terms/subject> ?topics . } UNION { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> ?redir . ?redir <http://purl.org/dc/terms/subject> ?topics . } }'),
('dbpedia_query', 4, 'debug', 'on'),
('dbpedia_query', 4, 'timeout', ''),
('dbpedia_query', 4, 'format', 'application/rdf+xml'),
('dbpedia_query', 4, 'save', 'display'),
('dbpedia_query', 5, 'query', 'CONSTRUCT { <http://dbpedia.org/resource/%TERM%> <http://www.w3.org/2002/07/owl#sameAs> ?uri } WHERE { { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> ?red . { ?red <http://www.w3.org/2002/07/owl#sameAs> ?uri } UNION { ?uri <http://www.w3.org/2002/07/owl#sameAs> ?red } } UNION { { <http://dbpedia.org/resource/%TERM%> <http://www.w3.org/2002/07/owl#sameAs> ?uri . } UNION { ?uri <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/%TERM%> } } }'),
('dbpedia_query', 5, 'debug', 'on'),
('dbpedia_query', 5, 'timeout', ''),
('dbpedia_query', 5, 'format', 'application/rdf+xml'),
('dbpedia_query', 5, 'save', 'display'),
('dbpedia_query', 6, 'query', 'CONSTRUCT { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/abstract> ?par } WHERE { { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/abstract> ?par . FILTER langMatches( lang(?par), "EN" ) } UNION { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> ?red . ?red <http://dbpedia.org/ontology/abstract> ?par . FILTER langMatches( lang(?par), "EN" )  } }'),
('dbpedia_query', 6, 'debug', 'on'),
('dbpedia_query', 6, 'timeout', ''),
('dbpedia_query', 6, 'format', 'application/rdf+xml'),
('dbpedia_query', 6, 'save', 'display'),
('dbpedia_foaf', 1, 'query', 'construct{ <http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/page> ?h. <http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/nick> ?n. <http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/homepage> ?p. <http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/depiction> ?i. <http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/thumbnail> ?z. <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageExternalLink> ?el. } where { {<http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/page> ?h.} union  {<http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/nick> ?n.} union  {<http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/homepage> ?p.} union  {<http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/depiction> [ <http://xmlns.com/foaf/0.1/thumbnail> ?z]. } union {<http://dbpedia.org/resource/%TERM%> <http://xmlns.com/foaf/0.1/depiction> ?i } UNION {<http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageExternalLink> ?el }}'),
('dbpedia_foaf', 1, 'debug', 'on'),
('dbpedia_foaf', 1, 'timeout', ''),
('dbpedia_foaf', 1, 'format', 'application/rdf+xml'),
('dbpedia_foaf', 1, 'save', 'display'),
('dbpedia_class', 1, 'query', 'CONSTRUCT { <http://dbpedia.org/ontology/%TERM%> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?sup } WHERE { <http://dbpedia.org/ontology/%TERM%> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?sup }'),
('dbpedia_class', 1, 'debug', 'on'),
('dbpedia_class', 1, 'timeout', ''),
('dbpedia_class', 1, 'format', 'application/rdf+xml'),
('dbpedia_class', 1, 'save', 'display'),
('dbpedia_class', 2, 'query', 'CONSTRUCT { <http://dbpedia.org/ontology/%TERM%> <http://www.w3.org/2000/01/rdf-schema#superClassOf> ?sub } WHERE { ?sub <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://dbpedia.org/ontology/%TERM%> }'),
('dbpedia_class', 2, 'debug', 'on'),
('dbpedia_class', 2, 'timeout', ''),
('dbpedia_class', 2, 'format', 'application/rdf+xml'),
('dbpedia_class', 2, 'save', 'display'),
('dbpedia_prop', 1, 'query', 'CONSTRUCT { <http://dbpedia.org/resource/%TERM%> ?prop ?val . } WHERE { { <http://dbpedia.org/resource/%TERM%> ?prop ?val . } UNION { <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> [?prop ?val]. } FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/page") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/name") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/nick") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/homepage") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/depiction") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/primaryTopic") FILTER (str(?prop) != "http://www.w3.org/2002/07/owl#sameAs") FILTER (str(?prop) != "http://purl.org/dc/terms/subject") FILTER (str(?prop) != "http://www.w3.org/1999/02/22-rdf-syntax-ns#type") FILTER (str(?prop) != "http://www.w3.org/2000/01/rdf-schema#comment") FILTER (str(?prop) != "http://www.w3.org/2000/01/rdf-schema#label") FILTER (str(?prop) != "http://dbpedia.org/ontology/wikiPageExternalLink") FILTER (str(?prop) != "http://dbpedia.org/ontology/abstract" ) FILTER (str(?prop) != "http://dbpedia.org/ontology/thumbnail" ) FILTER (str(?prop) != "http://dbpedia.org/ontology/wikiPageDisambiguates" ) FILTER (str(?prop) != "http://dbpedia.org/ontology/wikiPageRedirects" ) FILTER (str(?prop) != "http://dbpedia.org/property/wikiPageUsesTemplate" ) } ORDER BY ASC(?prop) ASC(?val) '),
('dbpedia_prop', 1, 'debug', 'on'),
('dbpedia_prop', 1, 'timeout', ''),
('dbpedia_prop', 1, 'format', 'application/rdf+xml'),
('dbpedia_prop', 1, 'save', 'display'),
('dbpedia_prop', 2, 'query', 'CONSTRUCT { ?subj ?prop <http://dbpedia.org/resource/%TERM%> . }WHERE { { ?subj ?prop <http://dbpedia.org/resource/%TERM%> . } UNION { ?subj ?prop ?r . <http://dbpedia.org/resource/%TERM%> <http://dbpedia.org/ontology/wikiPageRedirects> ?r. } FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/page") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/name") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/nick") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/homepage") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/depiction") FILTER (str(?prop) != "http://xmlns.com/foaf/0.1/primaryTopic") FILTER (str(?prop) != "http://www.w3.org/2002/07/owl#sameAs") FILTER (str(?prop) != "http://purl.org/dc/terms/subject") FILTER (str(?prop) != "http://www.w3.org/1999/02/22-rdf-syntax-ns#type") FILTER (str(?prop) != "http://www.w3.org/2000/01/rdf-schema#comment") FILTER (str(?prop) != "http://www.w3.org/2000/01/rdf-schema#label") FILTER (str(?prop) != "http://dbpedia.org/ontology/wikiPageExternalLink") FILTER (str(?prop) != "http://dbpedia.org/ontology/abstract" ) FILTER (str(?prop) != "http://dbpedia.org/ontology/thumbnail" ) FILTER (str(?prop) != "http://dbpedia.org/ontology/wikiPageDisambiguates" ) FILTER (str(?prop) != "http://dbpedia.org/ontology/wikiPageRedirects" ) FILTER (str(?prop) != "http://dbpedia.org/property/wikiPageUsesTemplate" ) }ORDER BY ASC(?prop) ASC(?subj)'),
('dbpedia_prop', 2, 'debug', 'on'),
('dbpedia_prop', 2, 'timeout', ''),
('dbpedia_prop', 2, 'format', 'application/rdf+xml'),
('dbpedia_prop', 2, 'save', 'display'),
('dbpedia_instance', 1, 'query', 'Construct {?x rdf:type <%TERM%>} WHERE {  ?x rdf:type <%TERM%>}'),
('dbpedia_instance', 1, 'debug', 'on'),
('dbpedia_instance', 1, 'timeout', ''),
('dbpedia_instance', 1, 'format', 'application/rdf+xml'),
('dbpedia_instance', 1, 'save', 'display');

-- --------------------------------------------------------

--
-- Table structure for table `query`
--

CREATE TABLE IF NOT EXISTS `query` (
  `string` varchar(100) NOT NULL,
  `endpoint_name` varchar(20) NOT NULL,
  PRIMARY KEY (`string`),
  KEY `endpoint_name` (`endpoint_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `query`
--

INSERT INTO `query` (`string`, `endpoint_name`) VALUES
('dbpedia_query', 'dbpedia'),
('dbpedia_foaf', 'dbpedia'),
('dbpedia_class', 'dbpedia'),
('dbpedia_prop', 'dbpedia'),
('dbpedia_instance', 'dbpedia');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
