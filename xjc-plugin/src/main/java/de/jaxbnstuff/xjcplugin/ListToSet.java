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

public class ListToSet extends Plugin {

	@Override
	public String getOptionName() {
		return "Xinst-set";
	}

	@Override
	public String getUsage() {
		return "-Xinst-set: generate collections as Set<?> instead of List<?>";
	}

	@Override
	public boolean run(Outline outline, Options opt, ErrorHandler errorHandler)
			throws SAXException {

		
		//1. Store the types of all the generated classes
		List<JType> types = new ArrayList<JType>();
		List<String> names = new ArrayList<String>();
		for (ClassOutline co : outline.getClasses()){
			types.add(co.implClass);
			System.out.println("Added Type: " + co.implClass.fullName());
			
			for ( CPropertyInfo p :co.target.getProperties()){
				System.out.println("Disp Name " + p.displayName());
				System.out.println("Collection " + p.isCollection());
				System.out.println("Name " + p.getName(false));
				
				if (p.isCollection()){
//					System.out.println("Base Type Name" + p.baseType.name());
					names.add(p.getName(false));
					
				}
			}
		}
		
		for (ClassOutline co : outline.getClasses()){
			//2. Look through the fields defined in each of the classes
			Map<String, JFieldVar> fields = co.implClass.fields();

			for (JFieldVar f : fields.values()){
				
				JClass fClass = (JClass) f.type();
				
				if (fClass.getTypeParameters()!=null && 
						fClass.getTypeParameters().size()==1){
					
					// f.type() is a list
					JType inner = fClass.getTypeParameters().get(0);
					
					JType setType = co.parent().getCodeModel().ref(Set.class).narrow(inner);
					f.type(setType);
					
					replaceGetter(co, f, inner);
					
				}
							
				
				
			}
		}
		
		return true;
		
	}
	
	
	private void replaceGetter(ClassOutline co, JFieldVar f, JType inner) {
		//Create the method name
		String get = "get";
		String name  = f.name().substring(0, 1).toUpperCase() 
				+ f.name().substring(1);
		String methodName = get+name;
		
		//Create HashSet JType
		JType hashSetType = co.parent().getCodeModel().ref(HashSet.class).narrow(inner);
		
		//Find and remove Old Getter!
		JMethod oldGetter = co.implClass.getMethod(methodName, new JType[0]);
		co.implClass.methods().remove(oldGetter);
		
		//Create New Getter
		JMethod getter = co.implClass.method(JMod.PUBLIC, f.type(), methodName);
		
		//Create Getter Body -> {if (f = null) f = new HashSet(); return f;}
		getter.body()._if(JExpr.ref(f.name()).eq(JExpr._null()))._then()
		.assign(f, JExpr._new(hashSetType));
				
		getter.body()._return(JExpr.ref(f.name()));
	}

}
