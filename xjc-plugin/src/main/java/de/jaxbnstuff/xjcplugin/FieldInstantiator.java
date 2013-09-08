package de.jaxbnstuff.xjcplugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JNullType;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

/**
 * This plug-in finds fields of types that are defined in the schema and instantiates them.
 *
 */
public class FieldInstantiator extends Plugin 
{
	Logger logger = Logger.getLogger(FieldInstantiator.class.getName());

	@Override
	public String getOptionName() {
		return "Xinst-fields";
	}

	@Override
	public String getUsage() {
		return "-Xinst-fields: Instantiate all fields with Types defined in the Schema.";
	}

	@Override
	public boolean run(Outline outline, Options opt, ErrorHandler errorHandler)
			throws SAXException {
		
		//1. Store the types of all the generated classes
		List<JType> types = new ArrayList<JType>();

		for (ClassOutline co : outline.getClasses()){
			types.add(co.implClass);
			System.out.println("Added Type: " + co.implClass.fullName());
		}
		
		for (ClassOutline co : outline.getClasses()){
			//2. Look through the fields defined in each of the classes
			Map<String, JFieldVar> fields = co.implClass.fields();

			for (JFieldVar f : fields.values()){
				if (types.contains(f.type())){ 
					//If the type is defined in the schema
					//3. Instantiate
					f.init(JExpr._new(f.type())); 
				} 
			}
		}
		
		return true;
	}
	
}