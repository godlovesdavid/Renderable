package Approach;

import java.io.*;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/*
 * TODO: Enum declaration, figure out 
 */

/**
 * Interface RenderNode
 */
interface RenderNode //Is public in C#
{
    String render();
    void render(OutputStreamWriter OutputStream);
    RenderNode stream(RenderNode RenderStream);
    void stream(OutputStreamWriter OutputStream);

    void RenderHead(OutputStreamWriter OutputStream);
    void RenderCorpus(OutputStreamWriter OutputStream);
    void RenderTail(OutputStreamWriter OutputStream);
    void Add(RenderNode incoming);
}

/**
 * Class Renderable
 */
public class Renderable implements RenderNode 
{
	static long recent;//{ get;set;}
    static long cursor;//{ get;set;}
    final long accessor = recent;
    public ArrayList<RenderNode> children = new ArrayList<RenderNode>(); //Undefined

    public Renderable() { recent++; cursor = recent; }

    /* CLASS ACTIONS */

    public RenderNode stream(RenderNode RenderStream)
    {
        for(RenderNode child : children)
            RenderStream.Add((Renderable)child);
        return (Renderable)RenderStream;
    }
    
    public void stream(OutputStreamWriter OutputStream)
    {
        this.RenderHead(OutputStream);
        this.RenderCorpus(OutputStream);
        this.RenderTail(OutputStream);
    }

    public void prerender(OutputStreamWriter OutputStream)
    {
        RenderHead(OutputStream);
        RenderCorpus(OutputStream);
    }
    
    public String render()
    {
        OutputStreamWriter InstancedStream = new OutputStreamWriter(System.out);
        this.render(InstancedStream);
        return InstancedStream.toString();
    }
    
    public void render(OutputStreamWriter OutputStream)
    {
        this.RenderHead(OutputStream);
        this.RenderCorpus(OutputStream);
        this.RenderTail(OutputStream);
    }
    
    public void RenderHead(OutputStreamWriter OutputStream)
    {
        try 
        {
			OutputStream.write((int)this.accessor); //Had to cast long to int here
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		} 
        try 
        {
			OutputStream.write(this.getClass().toString()); //double check this
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		} 
    }
    
    public void RenderCorpus(OutputStreamWriter OutputStream)
    {
        try 
        {
			OutputStream.write(children.size());
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
        for (RenderNode child : this.children)
            ((Renderable)(child)).render(OutputStream);
    }
    
    public void RenderTail(OutputStreamWriter OutputStream)
    {
        //optional (null terminate for C stream or structuring)
        try 
        {
			OutputStream.write('\0');
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
        
    }

    public void Add(RenderNode incoming) 
    { 
    	children.add(incoming); 
    }
}

/**
 * Class RenderXML
 */
class RenderXML extends Renderable  
{
    public String tag, id, content;
    public ArrayList<String> classes = new ArrayList<String>();
    public Map<String, String> attributes = new HashMap<String, String>();
    
    protected ArrayList<RenderNode> children = new ArrayList<RenderNode>();

    public enum RenderFlags
    {
       SelfContained(0x1),
       OpenRendered(0x2),
       ContentRendered(0x4),
       ContentOff(0x8),
       ContentOnly(0x10),
       EnsureChildren(0x20),
       Listening(0x40),
       PerforationPoint(0x80);
       
       private int code;
       
       private RenderFlags(int c)
       {
    	   code = c;
       }
       
       public int getCode()
       {
    	   return code;
       }
    };

    public EnumSet<RenderFlags> flags;
    RenderXML(
        String _tag, String _id, String _content,
        ArrayList<String> _classes, Map<String, String> _attributes,
        EnumSet<RenderFlags> _flags, Map<String, ?> options)
        /*: base()*/
    {
    	
        /* initializations moved here */
        _id = null;
        _content = null;
        _classes = null;
        _attributes = null;
        _flags.clear();
        options = null;
        
        flags.clear();//Clearing flags
        
        tag = _tag;
        if (_id != null) id = _id;
        if (_classes != null) classes = _classes;
        if (_attributes != null) attributes = _attributes;
        if (_content != null) content = _content;
        if (!_flags.isEmpty()) flags.addAll(_flags);
    }

    public String BuildAttributes()
    {
        String attr_format = "";
        for(Map.Entry<String, String> attr : attributes.entrySet()) //Need to double check this
        {
            attr_format += " " + attr.getKey() + "=\"" + attr.getValue() + "\"";
        }
        return attr_format;
    }
    
    public String BuildClasses(String class_format)
    {
    	class_format = " class=\""; //defining this here instead of in parameters
        if (this.classes.size() > 0)
        {
            for (String css_class : this.classes)
            {
                class_format += css_class + " ";
            }
            return class_format + "\"";
        }
        else return "";
    }

    public /*new*/ void RenderHead(OutputStreamWriter OutputStream)
    {
        if (!this.flags.contains(RenderFlags.ContentOnly) && !this.flags.contains(RenderFlags.OpenRendered))
        {
            try 
            {
				OutputStream.write(
				    "<" +
				        this.tag +
				        (this.id != null ? " " + id : "") +
				        (this.classes.size() > 0 ? " " + this.BuildClasses("") : "") +
				        (this.attributes.size() > 0 ? " " + this.BuildAttributes() : "") +
				        (this.flags.contains(RenderFlags.SelfContained) ? " /" : "") +
				    ">");
			} 
            catch (IOException e) 
            {
				e.printStackTrace();
			}
            this.flags.add(RenderFlags.OpenRendered); //Set OpenRendered flag on
        }
    }
    
    public /*new*/ void RenderCorpus(OutputStreamWriter OutputStream)
    {
        if (!this.flags.contains(RenderFlags.ContentRendered))
        {
            if (!this.flags.contains(RenderFlags.ContentOff))
            {
                try 
                {
					OutputStream.write(this.content);
				} 
                catch (IOException e) {
					e.printStackTrace();
				}
                //this.flags = this.flags & RenderFlags.ContentRendered;
                //Is above line same as below?
                
            }

            if (!this.flags.contains(RenderFlags.PerforationPoint) || this.flags.contains(RenderFlags.EnsureChildren))
            { //flags
                for (RenderNode child : this.children) //changed var to RenderNode
                {
                    ((RenderXML)(child)).render(OutputStream);
                }
            }
            this.flags.add(RenderFlags.ContentRendered); //Set ContentRendered flag on
        }
    }
    
    public /*new*/ void RenderTail(OutputStreamWriter OutputStream)
    {
        if (!this.flags.contains(RenderFlags.SelfContained))
			try 
        	{
				OutputStream.write("</" + this.tag + ">");
			} 
        	catch (IOException e) 
        	{
				e.printStackTrace();
			}
    }
    
    public /*new*/ void prerender(OutputStreamWriter OutputStream)
    {
        RenderHead(OutputStream);
        RenderCorpus(OutputStream);
    }
    
    public /*new*/ String render()
    {
        OutputStreamWriter InstancedStream = new OutputStreamWriter(System.out);
        this.render(InstancedStream);
        return InstancedStream.toString();
    }
    
    public /*new*/ void render(OutputStreamWriter OutputStream)
    {
        RenderHead(OutputStream);
        RenderCorpus(OutputStream);
        RenderTail(OutputStream);
    }

    public /*new*/ RenderXML stream(RenderNode RenderStream)
    {
        for (RenderNode child : children)
        {
            RenderStream.Add((RenderXML)child);
        }
        return (RenderXML)RenderStream;
    }
    public /*new*/ void stream(OutputStreamWriter OutputStream)
    {
        this.RenderHead(OutputStream);
        this.RenderCorpus(OutputStream);
        this.RenderTail(OutputStream);
    }

    /* CLASS ACTIONS */
    public void Add(RenderXML incoming) { children.add(incoming); }
}

/**
 * Class JSONable
 */
class JSONable extends Renderable
{
//[FlagsAttribute]
	public enum JSONableFlags
    {
        ObjMember(0x1),
        ArrayMember(0x2),
        StringMember(0x4),
        DecimalMember(0x8),
        IntMember(0x10),
        OpenRendered(0x20),
        ContentRendered(0x40),
        ContentOnly(0x80),
        EnsureChildren(0x100),
        Listening(0x200),
        PerforationPoint(0x400);
        
        private int code;
        
        private JSONableFlags(int c)
        {
        	code = c;
        }
        
        public int getCode()
        {
        	return code;
        } 
    };
    
    public EnumSet<JSONableFlags> flags;

    public boolean IsArray;
    public long counter, cursor;
    public Map<String, Long> properties = new HashMap<String, Long>();

    protected Map<Map<Long, String>, Long> IntegerValues; 
    protected Map<Map<Long, String>, Double> DecimalValues;
    protected Map<Map<Long, String>, String> StringValues;
    protected /*new*/ Map<Map<Long, String>, Map<Boolean, JSONable>> children;
    
    private Map<Long, Short> TotalOrdering;

    public JSONable(boolean _IsArray) /*: base()*/ //Need to figure out
    {
    	_IsArray = false; //defining here instead of in parameters
    	counter = 0; cursor = 0; TotalOrdering = new HashMap<Long, Short>();
    	IsArray = _IsArray;

    	IntegerValues = new HashMap<Map<Long, String>, Long>();
    	DecimalValues = new HashMap<Map<Long, String>, Double>();
    	StringValues = new HashMap<Map<Long, String>, String>();
    	children = new HashMap<Map<Long, String>, Map<Boolean, JSONable>>();
    	TotalOrdering = new HashMap<Long, Short>(); //This is a duplicate line as a few lines above, should that be changed?
    }
    
    public JSONable(ArrayList<?> _properties, boolean _IsArray) /*: base()*/ //Need to figure out
    {
    	_IsArray = false; //defining here instead of in parameters
    	counter = 0; cursor = 0; TotalOrdering = new HashMap<Long, Short>();
    	IsArray = _IsArray;

    	IntegerValues = new HashMap<Map<Long, String>, Long>();
    	DecimalValues = new HashMap<Map<Long, String>, Double>();
    	StringValues = new HashMap<Map<Long, String>, String>();
    	children = new HashMap<Map<Long, String>, Map<Boolean, JSONable>>();
    	TotalOrdering = new HashMap<Long, Short>(); //This is a duplicate line as a few lines above, should that be changed?

        for (Object property : _properties)
        {
			String TypeInUse = property.getClass().toString();

            //Direct Values
            if (TypeInUse.equals(Long.class))
            {	
            	Map val = new HashMap<Long, String>();
            	val.put(counter, null);
                IntegerValues.put(val, (long)property);
                TotalOrdering.put(counter, (short)JSONableFlags.IntMember.getCode());
                counter++;
                continue;
            }
            if (TypeInUse.equals(Double.class))
            {
            	Map val = new HashMap<Long, String>();
            	val.put(counter, null);
                DecimalValues.put(val, (double)property);
                TotalOrdering.put(counter, (short)JSONableFlags.DecimalMember.getCode());
                counter++;
                continue;
            }
            if (TypeInUse.equals(String.class))
            {
            	Map val = new HashMap<Long, String>();
            	val.put(counter,  null);
                StringValues.put(val, (String)property);
                TotalOrdering.put(counter, (short)JSONableFlags.StringMember.getCode());
                counter++;
                continue;
            }
            if (TypeInUse.equals(/*HashMap<Boolean, JSONable>().class*/""))
            {
            	Map val = new HashMap<Long, String>();
            	val.put(counter, null);
                children.put(val, (Map<Boolean, JSONable>) property);
                if (property.getKey())          
                	TotalOrdering.put(counter, (short)JSONableFlags.ArrayMember.getCode());
                else 
                	TotalOrdering.put(counter, (short)JSONableFlags.ObjMember.getCode());
                counter++;
                continue;
            }

            //Key Value Pairs
            if (TypeInUse.equals(/*(HashMap<String, Long>()*/""))
            {      	
            	Map val = new HashMap<Long, String>();
            	val.put(counter, property.getKey());
                IntegerValues.put(val, property.getValue());
                TotalOrdering.put(counter, (short)JSONableFlags.IntMember.getCode());
                counter++;
                continue;
            }
            if (TypeInUse.equals(/*Map.Entry<String, Double>*/""))
            {
            	Map val = new HashMap<Long, String>();
            	val.put(counter, property.getKey());
                DecimalValues.put(val, property.getValue());
                TotalOrdering.put(counter, (short)JSONableFlags.DecimalMember.getCode());
                counter++;
                continue;
            }
            if (TypeInUse.equals(/*Map.Entry<String, String>*/""))
            {
            	Map val = new HashMap<Long, String>();
            	val.put(counter, property.getKey());
                StringValues.put(val, property.getValue());
                TotalOrdering.put(counter, (short)JSONableFlags.StringMember.getCode());
                counter++;
                continue;
            }

            if (TypeInUse.equals(/*Map.Entry<String, Map.Entry<Boolean, JSONable>*/""))
            {
            	Map val = new HashMap<Long, String>();
            	val.put(counter, property.getKey());//good first val
                children.put(val, property.getValue());
        

                if (property.getValue().getKey()) 
                	TotalOrdering.put(counter, (short)JSONableFlags.ArrayMember.getCode());
                else 
                	TotalOrdering.put(counter, (short)JSONableFlags.ObjMember.getCode());

                counter++;
                continue;
            }

            //last resort
            Map val = new HashMap<Long, String>();
            Map val2 = new HashMap<Boolean, JSONable>();
            val.put(counter,  null);
            val2.put(false, null);
            children.put(val, val2);
            counter++;
        }
    }

    public /*new*/ void RenderHead(OutputStreamWriter OutputStream)
    {
        if (!this.flags.contains(JSONableFlags.ContentOnly) && !this.flags.contains(JSONableFlags.OpenRendered))
        {
            if (IsArray)
				try 
            	{
					OutputStream.write("[");
				} 
            	catch (IOException e) 
            	{
					e.printStackTrace();
				}
			else
				try 
            	{
					OutputStream.write("{");
				} 
            	catch (IOException e) 
				{
					e.printStackTrace();
				}

            this.flags.add(JSONableFlags.OpenRendered); //Set OpenRendered flag on
        }
    }
    public /*new*/ void RenderCorpus(OutputStreamWriter OutputStream)
    {
        if (!this.flags.contains(JSONableFlags.ContentRendered))
        {
            for (/*KeyValuePair*/Map.Entry<Long, Short> item : TotalOrdering)
            {
                if (item.getKey() != 0) OutputStream.write(",");
                switch (item.getValue())
                {
                    case (short)JSONableFlags.IntMember.getCode():
                                                    for (/*KeyValuePair*/Map.Entry<Long, String> k : IntegerValues.keySet())
                                                    {
                                                        if (item.getKey() == k.getKey()){ OutputStream.Write("\"" + ((k.getValue() == null) ? k.getKey().toString() : k.getValue()) + "\" : " + IntegerValues[k]); }
                                                    } break;
                    case (short)JSONableFlags.DecimalMember.getCode():
                                                    for (/*KeyValuePair*/Map.Entry<Long, String> k : DecimalValues.keySet())
                                                    {
                                                        if (item.getKey() == k.getKey()) { OutputStream.Write("\"" + ((k.getValue() == null) ? k.getKey().toString() : k.getValue()) + "\" : " + DecimalValues[k]); }
                                                    } break;
                    case (short)JSONableFlags.StringMember.getCode():
                                                    for (/*KeyValuePair*/Map.Entry<Long, String> k : StringValues.keySet())
                                                    {
                                                        if (item.getKey() == k.getKey()) { OutputStream.Write("\"" + ((k.getValue() == null) ? k.getKey().toString() : k.getValue()) + "\" : \"" + StringValues[k] + "\""); }
                                                    } break;
                    case (short)JSONableFlags.ArrayMember.getCode():
                    case (short)JSONableFlags.ObjMember.getCode():
                                                    foreach (/*KeyValuePair*/Map.Entry<Long, String> k : children.keySet())
                                                    {
                                                        if (item.getKey() == k.getKey())
                                                        {
                                                            OutputStream.write(((k.getValue() == null) ? ((IsArray) ? "" : "\"" + k.getKey().toString() + "\"") : "\"" + k.getValue() + "\"") + " : ");
                                                            children[k].getValue().render(OutputStream);
                                                       }
                                                    } break;
                    default: break;
                }
            }

            this.flags.add(JSONableFlags.ContentRendered); //Set ContentRendered flag on
        }
    }
    public /*new*/ void RenderTail(OutputStreamWriter OutputStream)
    {
        if (!this.flags.contains(JSONableFlags.ContentOnly))
        {
            if (IsArray)
				try 
            	{
					OutputStream.write("]");
				} 
            	catch (IOException e) 
            	{
					e.printStackTrace();
				}
			else
				try 
            	{
					OutputStream.write("}");
				} 
            	catch (IOException e) 
            	{
					e.printStackTrace();
				}
        }
    }
    public /*new*/ void prerender(OutputStreamWriter OutputStream)
    {
        RenderHead(OutputStream);
        RenderCorpus(OutputStream);
    }
    public /*new*/ String render()
    {
        OutputStreamWriter InstancedStream = new OutputStreamWriter(System.out);
        this.render(InstancedStream);
        return InstancedStream.toString();
    }
    public /*new*/ void render(OutputStreamWriter OutputStream)
    {
        RenderHead(OutputStream);
        RenderCorpus(OutputStream);
        RenderTail(OutputStream);
    }

    public /*new*/ JSONable stream(RenderNode RenderStream)
    {
        RenderStream.Add(this);
        return (JSONable)RenderStream;
    }
    public /*new*/ void stream(OutputStreamWriter OutputStream)
    {
        this.RenderHead(OutputStream);
        this.RenderCorpus(OutputStream);
        this.RenderTail(OutputStream);
    }

    /* CLASS ACTIONS */
    public void Add(JSONable incoming)
    {
    	Map val = new HashMap<Long, String>();
    	Map val2 = new HashMap<Boolean, JSONable>();
    	val.put(counter, null);
    	val2.put(incoming.IsArray, incoming);
        children.put(val, val2);
        TotalOrdering.put(counter, (incoming.IsArray) ? (short)JSONableFlags.ArrayMember.getCode() : (short)JSONableFlags.ObjMember.getCode());
        counter++;
    }
    public void Add(String key, JSONable incoming)
    {
    	Map val = new HashMap<Long, String>();
    	Map val2 = new HashMap<Boolean, JSONable>();
    	val.put(counter,  key);
    	val2.put(incoming.IsArray, incoming);
        children.put(val, val2);
        TotalOrdering.put(counter, (incoming.IsArray) ? (short)JSONableFlags.ArrayMember.getCode() : (short)JSONableFlags.ObjMember.getCode());
        counter++;
    }
}


