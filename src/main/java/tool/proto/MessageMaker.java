package tool.proto;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class MessageMaker {
	
	private final Map<String, ProtoMessage> messages;
	
	public MessageMaker(Map<String, ProtoMessage> messages) {
		this.messages = messages;
	}
	
	public void output(String dir, String path, String protoPath) {
		try {
			for (ProtoMessage message : messages.values()) {
				if ((message.isServerProcessor() || message.isClientProcessor()) && message.getFields().size() > 0) {
					outputRequests(message, dir, path, protoPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void outputRequests(ProtoMessage message, String dir, String path, String protoPath) throws Exception {
		String[] names = message.getName().split("_");
		StringBuilder nameBuilder = new StringBuilder();
		for (String nam : names) {
			nameBuilder.append(JavaFilesMaker.firstUpper(nam.toLowerCase()));
		}
		String name = nameBuilder.toString();
		String javaName = name;
		File java = new File(JavaFilesMaker.checkString(dir) ? dir : "../../../CrossGateBase/src");
		java = new File(java, path);
		java.mkdirs();
		java = JavaFilesMaker.createFile(java, javaName + ".java");
		String importPre = "import " + protoPath.replaceAll("/", ".") + ".";
		
		StringBuilder builder = new StringBuilder("package "), elementBuilder = new StringBuilder("\r\n");
		builder.append(path.replaceAll("/", ".")).append(";").append("\r\n");
		builder.append("\r\n");
		String param = "builder";
		String paramDefine = message.getName() + ".Builder " + param;
		elementBuilder.append("\t").append("private ").append(paramDefine).append(";").append("\r\n");
		
		StringBuilder importBuilder = new StringBuilder(), methodsBuilder = new StringBuilder();
		importBuilder.append("import net.dipatch.ISender;").append("\r\n");
		importBuilder.append("import net.io.protocal.proto.ProtoMessage;").append("\r\n");
		importBuilder.append(importPre).append(message.getProtoName()).append("Protos.*;").append("\r\n");
		Map<String, String> imports = Maps.newHashMap();
		imports.put(message.getProtoName(), message.getProtoName());
		
		for (ProtoMessageField field : message.getFields().values()) {
			String className = JavaFilesMaker.getProtoClassName(field.className);
			if (messages.containsKey(className)) {
				String importFile = messages.get(className).getProtoName();
				if (!imports.containsKey(importFile)) {
					imports.put(importFile, importFile);
					importBuilder.append(importPre).append(importFile).append("Protos.*;").append("\r\n");
				}
			}
			
			buildSet(methodsBuilder, importBuilder, field, imports, param);
			buildGet(methodsBuilder, importBuilder, field, imports, param);
		}
		
		methodsBuilder.append("\t").append("public ").append(message.getName()).append(" get").append(name).append("() {").append("\r\n");
		methodsBuilder.append("\t\t").append("return ").append(param).append(".build();").append("\r\n");
		methodsBuilder.append("\t").append("}").append("\r\n");
		methodsBuilder.append("\r\n");

		methodsBuilder.append("\t").append("@Override").append("\r\n");
		methodsBuilder.append("\t").append("public byte[] getByteArray() {").append("\r\n");
		methodsBuilder.append("\t\t").append("return ").append(param).append(".build().toByteArray();").append("\r\n");
		methodsBuilder.append("\t").append("}").append("\r\n");
		
		builder.append(importBuilder.toString());
		builder.append("\r\n");
		builder.append("/**").append("\r\n");
		builder.append(" * This is a auto make java file, so do not modify me.").append("\r\n");
		builder.append(" * @author fuhuiyuan").append("\r\n");
		builder.append(" */").append("\r\n");
		builder.append("public class ").append(javaName).append(" extends ProtoMessage {").append("\r\n");
		builder.append(elementBuilder.toString());
		builder.append("\r\n");
		builder.append("\t").append("public ").append(javaName).append("(int messageId, int status, String sessionId, ISender sender, byte[] datas) throws Exception {").append("\r\n");
		builder.append("\t\t").append("super(messageId, status, sessionId, sender, datas);").append("\r\n");
		builder.append("\t\t").append("if (datas != null) {").append("\r\n");
		builder.append("\t\t\t").append(param).append(".mergeFrom(datas);").append("\r\n");
		builder.append("\t\t").append("}").append("\r\n");
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
		String className = JavaFilesMaker.getProtoClassName(field.className);
		StringBuilder annotateBuilder = new StringBuilder(), methodBuilder = new StringBuilder();
		// annotate
		annotateBuilder.append("\t").append("/**").append("\r\n");
		annotateBuilder.append("\t").append(" * ").append("\r\n");
		annotateBuilder.append("\t").append(" * @return").append("\t").append(field.getAnnotate()).append("\r\n");
		annotateBuilder.append("\t").append(" */").append("\r\n");
		// method
		String getter = "get" + JavaFilesMaker.firstUpper(field.name);
		if (field.type.equals("required") || field.type.equals("optional")) {
			methodBuilder.append("\t").append("public ").append(checkClass(className)).append(" ").append(getter).append("() {").append("\r\n");
			methodBuilder.append("\t\t").append("return ").append(param).append(".").append(getter).append("();").append("\r\n");
		} else if (field.type.equals("repeated")) {
			JavaFilesMaker.addImport(imports, importBuilder, List.class);
			methodBuilder.append("\t").append("public List<").append(className).append("> ").append(getter).append("List() {").append("\r\n");
			methodBuilder.append("\t\t").append("return ").append(param).append(".").append(getter).append("List();").append("\r\n");
		}
		methodBuilder.append("\t").append("}").append("\r\n");
		methodBuilder.append("\r\n");
		// finish
		methodsBuilder.append(annotateBuilder.toString()).append(methodBuilder.toString());
	}
	
	private void buildSet(StringBuilder methodsBuilder, StringBuilder importBuilder, ProtoMessageField field, Map<String, String> imports, String param) {
		String className = JavaFilesMaker.getProtoClassName(field.className);
		StringBuilder annotateBuilder = new StringBuilder(), methodBuilder = new StringBuilder(), paramBulider = new StringBuilder();
		methodBuilder.append("\t").append("public void set").append(JavaFilesMaker.firstUpper(field.name)).append("(");
		// annotate
		annotateBuilder.append("\t").append("/**").append("\r\n");
		annotateBuilder.append("\t").append(" * ").append("\r\n");
		annotateBuilder.append("\t").append(" * @param").append("\t").append(field.name).append("\r\n");
		annotateBuilder.append("\t").append(" * ").append("\t\t\t").append(field.getAnnotate()).append("\r\n");
		annotateBuilder.append("\t").append(" */").append("\r\n");
		// method
		if (field.type.equals("required") || field.type.equals("optional")) {
			methodBuilder.append(className).append(" ");
			paramBulider.append("\t\t").append("builder.set").append(JavaFilesMaker.firstUpper(field.name)).append("(").append(field.name).append(");").append("\r\n");
		} else if (field.type.equals("repeated")) {
//			JavaFilesMaker.addImport(imports, importBuilder, Iterable.class);
			methodBuilder.append("Iterable<").append(className).append("> ");
			paramBulider.append("\t\t").append("builder.addAll").append(JavaFilesMaker.firstUpper(field.name)).append("(").append(field.name).append(");").append("\r\n");
		}
		methodBuilder.append(field.name).append(", ");
		methodBuilder.setLength(methodBuilder.length() - 2);
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
