package de.jaxbnstuff.xjcplugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

public class ListInstantiator extends Plugin {

	@Override
	public String getOptionName() {
		return "Xinst-lists";
	}

	@Override
	public String getUsage() {
		return "-Xinst-lists: instantiate all lists into ArrayLists.";
	}

	@Override
	public boolean run(Outline outline, Options opt, ErrorHandler errorHandler)
			throws SAXException {
				
				for (ClassOutline co : outline.getClasses()){
					//2. Look through the fields defined in each of the classes
					Map<String, JFieldVar> fields = co.implClass.fields();

					for (JFieldVar f : fields.values()){
						
						JClass fClass = (JClass) f.type();
						
						if (fClass.getTypeParameters()!=null && 
								fClass.getTypeParameters().size()==1){
							
							// f.type() is a list
							JType inner = fClass.getTypeParameters().get(0);
							
							
							f.init(JExpr._new(co.parent().getCodeModel()
									.ref(ArrayList.class).narrow(inner)));
							
							replaceGetterNoInst(co, f);	
						}
						
					}
				}
		return true;
	}

	/**
	 * Replaces the getter of field f, in classOutline co with one that does not
	 * check if the field is null.
	 * 
	 * @param co the ClassOutline of the class in which the getter is replaced
	 * @param f the Field for which the getter is replaced
	 */
	private void replaceGetterNoInst(ClassOutline co, JFieldVar f) {
		//Create the method name
		String get = "get";
		String name  = f.name().substring(0, 1).toUpperCase() 
				+ f.name().substring(1);
		String methodName = get+name;
		
		//Find and remove Old Getter!
		JMethod oldGetter = co.implClass.getMethod(methodName, new JType[0]);
		co.implClass.methods().remove(oldGetter);
		
		//Create New Getter
		JMethod getter = co.implClass.method(JMod.PUBLIC, f.type(), methodName);
						
		getter.body()._return(JExpr.ref(f.name()));
	}

}
