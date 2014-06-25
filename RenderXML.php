<?php

/*
	Title: Renderale Class for Approach

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

class RenderXML
{
	public static $renderObjectIndex=0; //last renderable made
	public static $NoAutoRender=array('html', 'head', 'body', 'script', 'style', 'channel', 'rss', 'item','title'); //also self-contained
	
	public $id=null;
	public $pageID='';

	public $tag='div';
	public $classes=Array();
	public $attributes=Array();
	public $content=null; //content that the opening and closing tags surround
	public $children=Array(); //child tags

	public $prefix=''; 
	public $infix='';
	public $postfix='';
	
	public $selfContained=false; //as in <selfcontained />

	/**
	 *	Make a renderable object with tag label, page ID, and options (in an array).
	 *
	 *	@param $tag tag label of the renderable, as in <tag>
	 *	@param $pageID html tag ID attribute.
	 *	@param $options array of options, including template, content, whether selfcontained, and also attributes.
	 */
	function RenderXML($t='div', $pageID='', $options=array())
	{
		//regeister new renderable as new index.
		$this->id=RenderXML::$renderObjectIndex;
		RenderXML::$renderObjectIndex++;

		//merge label with options param to be processed, if label given is in array form.
		if( is_array($t) )
		{
			$options = array_merge($t,$options); 
			$this->tag= isset($options['tag']) ? $options['tag'] : 'div';
		}
		else $this->tag = $t;
	
		//merge id with options param as above.
		if( is_array($pageID) )
		{ 
			$options = array_merge($pageID,$options); 
			$this->pageID= isset($options['pageID']) ? $options['pageID'] : get_class($this) . $this->id;
		}
		else $this->pageID = $pageID;
			
		//get attributes and options as given by options param.
		if(isset($options['pageID']) )	$this->pageID = $options['pageID'];
		if(isset($options['template'])) $this->content = GetFile($options['template']);
		if(isset($options['classes']) ) $this->classes = $options['classes'];
		if(isset($options['attributes'])) $this->attributes = $options['attributes'];
		if(isset($options['selfcontained'])) $this->selfContained = $options['selfcontained'];
		if(isset($options['content'])) $this->content = $options['content'] . $this->content;
		if(in_array($this->tag,RenderXML::$NoAutoRender)) $this->pageID='';
	}

	/**
	 *	Write out the classes attributes as string: class="class1 class2 etc".
	 */
	public function buildClasses()
	{
		$classesToString='';
		//classes are in an array.
		if(is_array($this->classes) && count($this->classes) >0 )
		{
			foreach($this->classes as $style)
				$classesToString .= $style . ' ';
				
			return $this->classes=' class="'.trim($classesToString).'" ';
		}
		//a one-class string.
		elseif(is_string($this->classes) && $this->classes != '')
		{
			return $this->classes = ' class="'.trim($this->classes).'" ';
		}
		//is a no-auto-render tag? Affix no class.
		elseif(in_array($this->tag,RenderXML::$NoAutoRender))
		{ 
			return ''; 
		}
		//for all else, affix "renderable renderable_$id" as class.
		else
		{
			return ' class="'.get_class($this) .' '. get_class($this) .'_'.$this->id . '" ';
		}
	}

	/**
	 *	Write out this renderable's attributes to string, as in attribute1="value" attribute2="value"
	 */
	public function buildAttributes()
	{
		$attribsToString=' ';
		
		//attributes is array case.
		if(is_array($this->attributes) )
		{
			foreach($this->attributes as $att=>$val)
			{
				//further parse if value is also an array.
				if(is_array($val) )
				{
					foreach($val as $_att=>$_val)
					{
						$attribsToString .= $_att . '="'.$_val.'" ';
					}
						
					return $this->attributes=$attribsToString;
				}
				
				//build string normally if not array.
				else $attribsToString .= $att . '="'.$val.'" ';
			}
			return $this->attributes=$attribsToString;
		}
		
		//attributes is string case.
		else if(is_string($this->attributes))	return ' '.$this->attributes.' ';	
		
		//attributes is unexpectedly neither string nor array.
		else	$attribsToString = ' data-approach-error="ATTRIBUTE_RENDER_ERROR" ';
		
		//re-set attributes to its proper format, and return it.
		return $this->attributes=$attribsToString;
	}

	/**
	 *	Render children and their contents.
	 */
	public function buildContent()
	{
		foreach($this->children as $renderObject)
			$this->content .= $renderObject->render();
	}
	
	/**
	 *	Render this renderable and its children, writing their html tags and their contents.
	 */
	public function render()
	{
		$OutputStream='';
		$this->buildContent();
 
		$OutputStream = $this->prefix . 
			'<' . $this->tag . //open
			( $this->pageID != ''		?	' id="'.$this->pageID.'" '	: '')	.
			( isset($this->attributes)	?	$this->buildAttributes()	: '')	.
			( isset($this->classes)		?	$this->buildClasses()		: '')	.
			($this->selfContained 		?	'/>'.PHP_EOL				: '>'	.
			$this->content . $this->infix.
			
			'</' . $this->tag . '>' . PHP_EOL) . //close
			$this->postfix;

		return $OutputStream;
	}
}


?>