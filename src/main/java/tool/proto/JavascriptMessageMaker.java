package tool.proto;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import com.google.common.collect.Maps;

public class JavascriptMessageMaker {
	
	private final Map<String, ProtoMessage> messages;
	
	public JavascriptMessageMaker(Map<String, ProtoMessage> messages) {
		this.messages = messages;
	}
	
	public void output(String messagdPath, String protoPath) {
		try {
			for (ProtoMessage message : messages.values()) {
				if (message.isServerProcessor() || message.isClientProcessor()) {
					if (message.getFields().size() > 0) {
						outputMessage(message, messagdPath, protoPath);
					} else {
						outputEmptyMessage(message, messagdPath, protoPath);
					}
				} else {
					outputVO(message, messagdPath, protoPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void outputVO(ProtoMessage message, String messagdPath, String protoPath) throws Exception {
		String name = JavaFilesMaker.makeJavascriptClassName(message.getName());
		String javaName = name;
		File java = new File(messagdPath);
		java.mkdirs();
		java = JavaFilesMaker.createFile(java, javaName + ".js");
		String importPre = "import " + protoPath.replaceAll("/", ".") + ".";
		
		StringBuilder builder = new StringBuilder("#pragma strict"), elementBuilder = new StringBuilder("\r\n");
		builder.append("\r\n");
		String param1 = "builder";
		String paramDefine1 = param1 + " : " + message.getName();
		elementBuilder.append("\t").append("private var ").append(paramDefine1).append(";").append("\r\n");
		elementBuilder.append("\r\n");
		
		StringBuilder importBuilder = new StringBuilder(), methodsBuilder = new StringBuilder();
		Map<String, String> imports = Maps.newHashMap();
		
		for (ProtoMessageField field : message.getFields().values()) {
			buildSet(methodsBuilder, importBuilder, field, imports, param1, importPre);
			buildGet(methodsBuilder, importBuilder, field, imports, param1);
		}
		
		methodsBuilder.append("\t").append("function ").append(" get").append(message.getName()).append("() : ").append(message.getName()).append("{").append("\r\n");
		methodsBuilder.append("\t\t").append("return ").append(param1).append(";").append("\r\n");
		methodsBuilder.append("\t").append("}").append("\r\n");
		methodsBuilder.append("\r\n");
		
		builder.append(importBuilder.toString());
		builder.append("\r\n");
		builder.append("/**").append("\r\n");
		builder.append(" * This is a auto make java file, so do not modify me.").append("\r\n");
		builder.append(" * @author fuhuiyuan").append("\r\n");
		builder.append(" */").append("\r\n");
		builder.append("public class ").append(javaName).append(" {").append("\r\n");
		builder.append(elementBuilder.toString());
		builder.append("\r\n");
		builder.append("\t").append("function ").append(javaName).append("(").append(paramDefine1).append(") {").append("\r\n");
		builder.append("\t\t").append("this.").append(param1).append(" = ").append(param1).append(";").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("function ").append(javaName).append("() {").append("\r\n");
		builder.append("\t\t").append(param1).append(" = new ").append(message.getName()).append("();").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append(methodsBuilder.toString());
		builder.append("}").append("\r\n");
		
		FileWriter writer = new FileWriter(java);
		writer.append(builder.toString());
		writer.flush();
		writer.close();
	}
	
	private void outputEmptyMessage(ProtoMessage message, String messagdPath, String protoPath) throws Exception {
		String name = JavaFilesMaker.makeJavascriptClassName(message.getName());
		String javaName = name;
		File java = new File(messagdPath);
		java.mkdirs();
		java = JavaFilesMaker.createFile(java, javaName + ".js");
		
		StringBuilder builder = new StringBuilder("#pragma strict");
		builder.append("\r\n");
		
		StringBuilder importBuilder = new StringBuilder();
		importBuilder.append("import net.io;").append("\r\n");
		
		builder.append(importBuilder.toString());
		builder.append("\r\n");
		builder.append("/**").append("\r\n");
		builder.append(" * This is a auto make java file, so do not modify me.").append("\r\n");
		builder.append(" * @author fuhuiyuan").append("\r\n");
		builder.append(" */").append("\r\n");
		builder.append("public class ").append(javaName).append(" extends ByteArrayMessage {").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("function ").append(javaName).append("(status : int, sessionId : String, sender : ISender, datas : byte[]) {").append("\r\n");
		builder.append("\t\t").append("ByteArrayMessage(Convert.ToInt32(MessageId.").append(message.getMessageIdName()).append("), status, sessionId, sender, datas);").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("function ").append(javaName).append("() {").append("\r\n");
		builder.append("\t\t").append("ByteArrayMessage();").append("\r\n");
		builder.append("\t\t").append("messageId = Convert.ToInt32(MessageId.").append(message.getMessageIdName()).append(");").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("}").append("\r\n");
		
		FileWriter writer = new FileWriter(java);
		writer.append(builder.toString());
		writer.flush();
		writer.close();
	}
	
	private void outputMessage(ProtoMessage message, String messagdPath, String protoPath) throws Exception {
		String name = JavaFilesMaker.makeJavascriptClassName(message.getName());
		String javaName = name;
		File java = new File(messagdPath);
		java.mkdirs();
		java = JavaFilesMaker.createFile(java, javaName + ".js");
		String importPre = "import " + protoPath.replaceAll("/", ".") + ".";
		
		StringBuilder builder = new StringBuilder("#pragma strict"), elementBuilder = new StringBuilder("\r\n");
		builder.append("\r\n");
		String param = "builder";
		String paramDefine = param + " : " + message.getName();
		elementBuilder.append("\t").append("private var ").append(paramDefine).append(";").append("\r\n");
		
		StringBuilder importBuilder = new StringBuilder(), methodsBuilder = new StringBuilder();
		importBuilder.append("import net.io;").append("\r\n");
		importBuilder.append("import cg.ba.io.proto;").append("\r\n");
		Map<String, String> imports = Maps.newHashMap();
		
		for (ProtoMessageField field : message.getFields().values()) {
			buildSet(methodsBuilder, importBuilder, field, imports, param, importPre);
			buildGet(methodsBuilder, importBuilder, field, imports, param);
		}
		
		methodsBuilder.append("\t").append("function ").append(" get").append(name).append("() : ").append(message.getName()).append(" {").append("\r\n");
		methodsBuilder.append("\t\t").append("return ").append(param).append(";").append("\r\n");
		methodsBuilder.append("\t").append("}").append("\r\n");
		methodsBuilder.append("\r\n");

		methodsBuilder.append("\t").append("function getByteArray() : byte[] {").append("\r\n");
		methodsBuilder.append("\t\t").append("return IOUtils.serializeProto(").append(param).append(");").append("\r\n");
		methodsBuilder.append("\t").append("}").append("\r\n");
		
		builder.append(importBuilder.toString());
		builder.append("\r\n");
		builder.append("/**").append("\r\n");
		builder.append(" * This is a auto make java file, so do not modify me.").append("\r\n");
		builder.append(" * @author fuhuiyuan").append("\r\n");
		builder.append(" */").append("\r\n");
		builder.append("class ").append(javaName).append(" extends ByteArrayMessage {").append("\r\n");
		builder.append(elementBuilder.toString());
		builder.append("\r\n");
		builder.append("\t").append("function ").append(javaName).append("(status : int, sessionId : String, sender : ISender, datas : byte[]) {").append("\r\n");
		builder.append("\t\t").append("ByteArrayMessage(Convert.ToInt32(MessageId.").append(message.getMessageIdName()).append("), status, sessionId, sender, datas);").append("\r\n");
		builder.append("\t\t").append("builder = new ").append(message.getName()).append("();").append("\r\n");
		builder.append("\t\t").append("if (datas != null) {").append("\r\n");
		builder.append("\t\t\t").append(param).append(" = IOUtils.deserializeProto(datas, ").append(message.getName()).append(");").append("\r\n");
		builder.append("\t\t").append("}").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("function ").append(javaName).append("() {").append("\r\n");
		builder.append("\t\t").append("ByteArrayMessage();").append("\r\n");
		builder.append("\t\t").append("builder = new ").append(message.getName()).append("();").append("\r\n");
		builder.append("\t\t").append("messageId = Convert.ToInt32(MessageId.").append(message.getMessageIdName()).append(");").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append(methodsBuilder.toString()).append("\r\n");
		builder.append("}").append("\r\n");
		
		FileWriter writer = new FileWriter(java);
		writer.append(builder.toString());
		writer.flush();
		writer.close();
	}
	
	private void buildGet(StringBuilder methodsBuilder, StringBuilder importBuilder, ProtoMessageField field, Map<String, String> imports, String param) {
		String className = JavaFilesMaker.getJavascriptClassName(field.className);
		String voClassName = JavaFilesMaker.makeJavascriptClassName(className);
		boolean isJavaStruct = JavaFilesMaker.isJavascriptStruct(className);
		StringBuilder annotateBuilder = new StringBuilder(), methodBuilder = new StringBuilder();
		// annotate
		annotateBuilder.append("\t").append("/**").append("\r\n");
		annotateBuilder.append("\t").append(" * ").append("\r\n");
		annotateBuilder.append("\t").append(" * @return").append("\t").append(field.getAnnotate()).append("\r\n");
		annotateBuilder.append("\t").append(" */").append("\r\n");
		// method
		String getter = "get" + JavaFilesMaker.firstUpper(field.name);
		if (field.type.equals("required") || field.type.equals("optional")) {
			String checkName = checkClass(voClassName);
			methodBuilder.append("\t").append("function ").append(getter).append("() : ").append(checkName).append(" { ").append("\r\n");
			if (isJavaStruct) {
				methodBuilder.append("\t\t").append("return ").append(param).append(".").append(field.name).append(";").append("\r\n");
			} else {
//				if (field.type.equals("optional")) {
//					methodBuilder.append("\t\t").append("return vo.has").append(JavaFilesMaker.firstUpper(field.name)).append("() ? new ").append(voClassName).append("(").append("vo.").append(getter).append("()) : null;").append("\r\n");
//				} else {
//					methodBuilder.append("\t\t").append("return new ").append(voClassName).append("(").append(param).append(".").append(getter).append("());").append("\r\n");
//				}
				methodBuilder.append("\t\t").append("return new ").append(voClassName).append("(").append(param).append(".").append(field.name).append(");").append("\r\n");
			}
		} else if (field.type.equals("repeated")) {
			JavaFilesMaker.addImport(imports, importBuilder, "IList", "System.Collections.Generic");
			if (isJavaStruct) {
				methodBuilder.append("\t").append("function ").append(getter).append("List() : IList.<").append(className).append("> {").append("\r\n");
				methodBuilder.append("\t\t").append("return ").append(param).append(".").append(field.name).append(";").append("\r\n");
			} else {
				methodBuilder.append("\t").append("function ").append(getter).append("List() : IList.<").append(voClassName).append("> {").append("\r\n");
				methodBuilder.append("\t\t").append("var list : ").append("IList.<").append(className).append("> = ").append(param).append(".").append(field.name).append(";").append("\r\n");
				methodBuilder.append("\t\t").append("var ret : ").append("IList.<").append(voClassName).append("> = new ArrayList(list.Count);").append("\r\n");
				methodBuilder.append("\t\t").append("for (var vo : ").append(className).append(" in list) {").append("\r\n");
				methodBuilder.append("\t\t\t").append("ret.Add(new ").append(voClassName).append("(vo));").append("\r\n");
				methodBuilder.append("\t\t").append("}").append("\r\n");
				methodBuilder.append("\t\t").append("return ret;").append("\r\n");
			}
		}
		methodBuilder.append("\t").append("}").append("\r\n");
		methodBuilder.append("\r\n");
		// finish
		methodsBuilder.append(annotateBuilder.toString()).append(methodBuilder.toString());
	}
	
	private void buildSet(StringBuilder methodsBuilder, StringBuilder importBuilder, ProtoMessageField field, Map<String, String> imports, String param, String importPre) {
		String className = JavaFilesMaker.getJavascriptClassName(field.className);
		StringBuilder annotateBuilder = new StringBuilder(), methodBuilder = new StringBuilder(), paramBulider = new StringBuilder();
		methodBuilder.append("\t").append("function set").append(JavaFilesMaker.firstUpper(field.name)).append("(").append(field.name).append(" : ");
		// annotate
		annotateBuilder.append("\t").append("/**").append("\r\n");
		annotateBuilder.append("\t").append(" * ").append("\r\n");
		annotateBuilder.append("\t").append(" * @param").append("\t").append(field.name).append("\r\n");
		annotateBuilder.append("\t").append(" * ").append("\t\t\t").append(field.getAnnotate()).append("\r\n");
		annotateBuilder.append("\t").append(" */").append("\r\n");
		// method
		String voClassName = JavaFilesMaker.makeJavascriptClassName(className);
		boolean isJavaStruct = JavaFilesMaker.isJavascriptStruct(className);
		if (field.type.equals("required") || field.type.equals("optional")) {
			if (field.type.equals("optional")) {
				paramBulider.append("\t\t").append("if (").append(field.name).append(" != null) {").append("\r\n");
				paramBulider.append("\t");
			}
			methodBuilder.append(voClassName);
			paramBulider.append("\t\t").append("builder.").append(field.name).append(" = ").append(field.name);
			if (isJavaStruct) {
				paramBulider.append(";");
			} else {
				paramBulider.append(".get").append(className).append("();");
			}
			paramBulider.append("\r\n");
			if (field.type.equals("optional")) {
				paramBulider.append("\t\t").append("}").append("\r\n");
			}
		} else if (field.type.equals("repeated")) {
			JavaFilesMaker.addImport(imports, importBuilder, "IList", "System.Collections.Generic");
			methodBuilder.append("IList.<").append(voClassName).append(">");
			if (isJavaStruct) {
//				paramBulider.append("\t\t").append("builder.").append(field.name).append(".Merge(").append(field.name).append(");").append("\r\n");
				paramBulider.append("\t\t").append("var list : ").append("IList.<").append(className).append("> = ").append("builder.").append(field.name).append(";").append("\r\n");
				paramBulider.append("\t\t").append("for (var ").append(" vo : ").append(voClassName).append(" in ").append(field.name).append(") {").append("\r\n");
				paramBulider.append("\t\t\t").append("list.Add(vo);").append("\r\n");
				paramBulider.append("\t\t").append("}").append("\r\n");
			} else {
//				if (messages.containsKey(className)) {
//					String importFile = messages.get(className).getProtoName();
//					if (!imports.containsKey(importFile)) {
//						imports.put(importFile, importFile);
//						importBuilder.append(importPre).append(importFile).append("Protos.*;").append("\r\n");
//					}
//				}
				
				paramBulider.append("\t\t").append("var list : ").append("IList.<").append(className).append("> = ").append("builder.").append(field.name).append(";").append("\r\n");
				paramBulider.append("\t\t").append("for (var vo : ").append(voClassName).append(" in ").append(field.name).append(") {").append("\r\n");
				paramBulider.append("\t\t\t").append("list.Add(vo.get").append(className).append("());").append("\r\n");
				paramBulider.append("\t\t").append("}").append("\r\n");
//				paramBulider.append("\t\t").append("builder.addAll").append(JavaFilesMaker.firstUpper(field.name)).append("(list);").append("\r\n");
			}
		}
		methodBuilder.append(") {").append("\r\n");
		methodBuilder.append(paramBulider.toString());
		methodBuilder.append("\t").append("}").append("\r\n");
		methodBuilder.append("\r\n");
		// finish
		methodsBuilder.append(annotateBuilder.toString()).append(methodBuilder.toString());
	}
	
	private String checkClass(String clssName) {
		if (clssName.equals("Integer")) {
			return "int";
		} else {
			return clssName;
		}
	}

}
