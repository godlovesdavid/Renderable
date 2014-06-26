<?php

/*
	Title: Renderable Utility Functions for Approach


	Copyright 2002-2014 Garet Claborn

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

*/


require_once('RenderXML.php');

$html = new RenderXML('html');
$html->tag='html';
$APPROACH_DOM_ROOT = 'html';	//Create better mechanism.


/*

These functions let you primarily search through types of class RenderXML by
common CSS selectors such as ID, Class, Attribute and Tag. 

Also the JavaScript Events have a require listed at the bottom of this source
JavaScript events need to look for your </head> element *or* the  </body> elemenet
and dynamically place event bindings, script linking or direct code at these 
locations.


Use 

$Collection = RenderSearch($anyRenderXML,'.Buttons'); 

Or Directly


$SingleTag=function GetRenderXML($SearchRoot, 1908);					   //System side render ID $RenderXML->id;
$SingleTag=function GetRenderXMLByPageID($root,'MainContent');			 //Client side page ID

$MultiElements=function GetRenderXMLsByClass($root, 'Buttons');
$MultiElements=function GetRenderXMLsByTag($root, 'div');

*/

/**
 *	Filter a 
 */
function filter( $tag, $content, $styles, $properties)
{
	$output="<" . $tag;
	foreach($this->$properties as $property => $value)
	{
		$output .= " $property=\"$value\"";
	}
	$output .= " class=\"";
	foreach($this->$styles as $class)
	{
		$output .= $class. " ";
	}
	$output .= "\" id=\"$tag\"" . $this->$id . '">';
	$output .=$content . "</$tag>";
}

//function _($root, $search){	return RenderSearch($root, $search); }

/**
 *	get render object by either id, page id, class, or tag.
 *
 *	@param RenderXML $root root renderable node to search
 *	@param Array $search [0] is selector ($, #, .) and [1] is the search text
 */
function RenderSearch($root, $search)
{
	$scope = $search[0];
	$search = substr($search, 1);
	$renderObject;
	switch($scope)
	{
		case '$': $renderObject=GetRenderXML($root, $search); break;
		case '#': $renderObject=GetRenderXMLByPageID($root, $search); break;
		case '.': $renderObject=GetRenderXMLsByClass($root, $search); break;
		default:  $renderObject=GetRenderXMLByTag($root, $search); break;
	}

	return $renderObject;
}

/**
 *	Get a RenderXML object by its id (not page id).
 *
 *	@param RenderXML $root root renderable node to search
 *	@param int $SearchID the id to search
 */
function GetRenderXML($root, $SearchID)
{
	if($root->id == $SearchID) return $root;

	foreach($root->children as $renderObject)
	{
		$result = GetRenderXML($renderObject,$SearchID);
		if($result instanceof RenderXML)
		{
			if($result->id == $SearchID) return $result;
		}
	}
}


/**
 *	Get a RenderXML object by tag.
 */
function GetRenderXMLsByTag($root, $tag)
{
	$Store=Array();

	foreach($root->children as $child)   //Get Head
	{
		if($child->tag == $tag)
		{
			$Store[]=$child;
		}
		foreach($child->$children as $children)
		{
			$Store = array_merge($Store, GetRenderXMLsByTag($children, $tag));
		}
	}
	return $Store;
}

/**
 *	Get RenderXML object by class.
 */
function GetRenderXMLsByClass($root, $class)
{
	$Store = array();

	foreach($root->children as $child)   //Get Head
	{
		$t=$child->classes;
		$child->buildClasses();

		if(strpos($child->classes,$class))
		{
			$Store[]=$child;
		}
		foreach($child->children as $children)
		{
			$Store = array_merge($Store, GetRenderXMLsByClass($children, $class));
		}
		$child->classes=$t;
	}
	return $Store;
}

/**
 *	Get a RenderXML object by its page id.
 */
function GetRenderXMLByPageID($root,$PageID)
{
	$Store = new RenderXML('div');
	$Store->pageID = 'DEFAULT_ID___ELEMENT_NOT_FOUND';
	foreach($root->children as $child)   //Get Head
	{
		if($child->pageID == $PageID)
		{
			$Store = $child;
			return $child;
		}
		foreach($child->children as $children)
		{
			$Store = GetRenderXMLByPageID($children, $PageID);
			if($Store->pageID == $PageID) return $Store;
		}
	}
	return $Store;
}

/**
 *	Get a RenderXML with tag name "head".
 */
function GetHeadFromDOM()
{
	global $APPROACH_DOM_ROOT;
	global $$APPROACH_DOM_ROOT;
	
	foreach($$APPROACH_DOM_ROOT->children as $child)   //Get Head
	{	
		if($child->tag == 'head')	return $child;	
	}
}

/**
 *	Get a RenderXML with tag name "body".
 */
function GetBodyFromDOM()
{
	global $APPROACH_DOM_ROOT;
	global $$APPROACH_DOM_ROOT;
	
	foreach($$APPROACH_DOM_ROOT->children as $child)   //Get Body
	{
		  if($child->tag == 'body')	return $child;	
	}
}

$ApproachDebugConsole = new RenderXML('div', 'ApproachDebugConsole');
$ApproachDebugMode = false;

?>
