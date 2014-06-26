<?php

require_once(__DIR__ . '/../Render.php');
//require_once(__DIR__ . '/../DataObject.php');
//require_once(__DIR__ . '../../core/Service.php');

$ApproachDisplayUnit = array();
$ApproachDisplayUnit['User']['Browser'] = new RenderXML('ul');

/**
 *
 */
class UserInterface extends RenderXML
{
	public $Layout;
	public $Header; 	//in layout
	public $Titlebar; 	//in header
	public $Content;	//in layout
	public $Footer;	//in layout
	
	UserInterface()
	{
		$this->tag					= 'ul'; 
		$this->classes[]			= 'Interface';
		$this->children[]			= $this->Layout 	= new RenderXML('li','',	array('classes'=>'Layout') );

		$this->Layout->children[]	= $this->Header 	= new RenderXML('ul','',	array('classes'=>array('Header','controls')));
		$this->Layout->children[]	= $this->Content	= new RenderXML('ul','',	array('classes'=>array('Content','controls')));
		$this->Layout->children[]	= $this->Footer		= new RenderXML('ul','',	array('classes'=>array('Footer','controls')));

		$this->Header->children[]	= $this->Titlebar	= new RenderXML('li','',	array('classes'=>array('Titlebar'),'content'=>'Complete action by following steps.'));
	}
}

/**
 *
 */
class Wizard extends UserInterface
{
	public $Slides;
	public $CancelButton;
	public $BackButton;
	public $NextButton;
	public $FinishButton;
	
	Wizard()
	{
		$this->classes[]		= 'Wizard';
		
		$Footer->children[]	= $CancelButton		= new RenderXML('li','',	array('classes'=>array('Cancel',	'DarkRed',		'Button'),'content'=>'Cancel'));
		$Footer->children[]	= $BackButton		= new RenderXML('li','',	array('classes'=>array('Back',	'DarkGreen',	'Button'),'content'=>'Back'));
		$Footer->children[]	= $NextButton		= new RenderXML('li','',	array('classes'=>array('Next',		'DarkGreen',	'Button'),'content'=>'Next'));
		$Footer->children[]	= $FinishButton		= new RenderXML('li','',	array('classes'=>array('Finish',	'DarkBlue',		'Button'),'content'=>'Finish'));

		$FinishButton->attributes['data-intent']='Autoform Insert ACTION';
	}
}

$ApproachDisplayUnit['Publication']['NewWizard'] = new Wizard();

?>