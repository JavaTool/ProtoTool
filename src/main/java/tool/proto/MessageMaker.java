package tool.proto;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MessageMaker {
	
	private final Map<String, ProtoMessage> messages;
	
	public MessageMaker(Map<String, ProtoMessage> messages) {
		this.messages = messages;
	}
	
	public void output(String dir, String path, String protoPath) {
		try {
			for (ProtoMessage message : messages.values()) {
				if (message.isServerProcessor() || message.isClientProcessor()) {
					if (message.getFields().size() > 0) {
						outputMessage(message, dir, path, protoPath);
					} else {
						outputEmptyMessage(message, dir, path, protoPath);
					}
				} else {
					outputVO(message, dir, path, protoPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void outputVO(ProtoMessage message, String dir, String path, String protoPath) throws Exception {
		String name = JavaFilesMaker.makeJavaClassName(message.getName());
		String javaName = name;
		File java = new File(JavaFilesMaker.checkString(dir) ? dir : "../../../CrossGateBase/src");
		java = new File(java, path);
		java.mkdirs();
		java = JavaFilesMaker.createFile(java, javaName + ".java");
		String importPre = "import " + protoPath.replaceAll("/", ".") + ".";
		
		StringBuilder builder = new StringBuilder("package "), elementBuilder = new StringBuilder("\r\n");
		builder.append(path.replaceAll("/", ".")).append(";").append("\r\n");
		builder.append("\r\n");
		String param1 = "vo";
		String paramDefine1 = message.getName() + " " + param1;
		elementBuilder.append("\t").append("private ").append(paramDefine1).append(";").append("\r\n");
		elementBuilder.append("\r\n");
		String param2 = "builder";
		String paramDefine2 = message.getName() + ".Builder " + param2;
		elementBuilder.append("\t").append("private ").append(paramDefine2).append(";").append("\r\n");
		
		StringBuilder importBuilder = new StringBuilder(), methodsBuilder = new StringBuilder();
		importBuilder.append(importPre).append(message.getProtoName()).append("Protos.*;").append("\r\n");
		Map<String, String> imports = Maps.newHashMap();
		imports.put(message.getProtoName(), message.getProtoName());
		
		for (ProtoMessageField field : message.getFields().values()) {
			buildSet(methodsBuilder, importBuilder, field, imports, param2, importPre);
			buildGet(methodsBuilder, importBuilder, field, imports, param1);
		}
		
		methodsBuilder.append("\t").append("public ").append(message.getName()).append(" get").append(message.getName()).append("() {").append("\r\n");
		methodsBuilder.append("\t\t").append("return ").append(param1).append(" == null ? ").append(param2).append(".build()").append(" : ").append(param1).append(";").append("\r\n");
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
		builder.append("\t").append("public ").append(javaName).append("(").append(paramDefine1).append(") {").append("\r\n");
		builder.append("\t\t").append("this.").append(param1).append(" = ").append(param1).append(";").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("public ").append(javaName).append("() {").append("\r\n");
		builder.append("\t\t").append(param2).append(" = ").append(message.getName()).append(".newBuilder();").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append(methodsBuilder.toString());
		builder.append("}").append("\r\n");
		
		FileWriter writer = new FileWriter(java);
		writer.append(builder.toString());
		writer.flush();
		writer.close();
	}
	
	private void outputEmptyMessage(ProtoMessage message, String dir, String path, String protoPath) throws Exception {
		String name = JavaFilesMaker.makeJavaClassName(message.getName());
		String javaName = name;
		File java = new File(JavaFilesMaker.checkString(dir) ? dir : "../../../CrossGateBase/src");
		java = new File(java, path);
		java.mkdirs();
		java = JavaFilesMaker.createFile(java, javaName + ".java");
		
		StringBuilder builder = new StringBuilder("package ");
		builder.append(path.replaceAll("/", ".")).append(";").append("\r\n");
		builder.append("\r\n");
		
		StringBuilder importBuilder = new StringBuilder();
		importBuilder.append("import net.dipatch.ISender;").append("\r\n");
		importBuilder.append("import net.io.protocal.proto.ProtoMessage;").append("\r\n");
		importBuilder.append("import cg.base.io.proto.MessageIdProto.MessageId;").append("\r\n");
		
		builder.append(importBuilder.toString());
		builder.append("\r\n");
		builder.append("/**").append("\r\n");
		builder.append(" * This is a auto make java file, so do not modify me.").append("\r\n");
		builder.append(" * @author fuhuiyuan").append("\r\n");
		builder.append(" */").append("\r\n");
		builder.append("public class ").append(javaName).append(" extends ProtoMessage {").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("public ").append(javaName).append("(int status, String sessionId, ISender sender, byte[] datas) throws Exception {").append("\r\n");
		builder.append("\t\t").append("super(MessageId.").append(message.getMessageIdName()).append("_VALUE, status, sessionId, sender, datas);").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("public ").append(javaName).append("() {").append("\r\n");
		builder.append("\t\t").append("super();").append("\r\n");
		builder.append("\t\t").append("messageId = MessageId.").append(message.getMessageIdName()).append("_VALUE;").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("}").append("\r\n");
		
		FileWriter writer = new FileWriter(java);
		writer.append(builder.toString());
		writer.flush();
		writer.close();
	}
	
	private void outputMessage(ProtoMessage message, String dir, String path, String protoPath) throws Exception {
		String name = JavaFilesMaker.makeJavaClassName(message.getName());
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
		importBuilder.append("import cg.base.io.proto.MessageIdProto.MessageId;").append("\r\n");
		Map<String, String> imports = Maps.newHashMap();
		imports.put(message.getProtoName(), message.getProtoName());
		
		for (ProtoMessageField field : message.getFields().values()) {
			buildSet(methodsBuilder, importBuilder, field, imports, param, importPre);
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
		builder.append("\t").append("public ").append(javaName).append("(int status, String sessionId, ISender sender, byte[] datas) throws Exception {").append("\r\n");
		builder.append("\t\t").append("super(MessageId.").append(message.getMessageIdName()).append("_VALUE, status, sessionId, sender, datas);").append("\r\n");
		builder.append("\t\t").append("builder = ").append(message.getName()).append(".newBuilder();").append("\r\n");
		builder.append("\t\t").append("if (datas != null) {").append("\r\n");
		builder.append("\t\t\t").append(param).append(".mergeFrom(datas);").append("\r\n");
		builder.append("\t\t").append("}").append("\r\n");
		builder.append("\t").append("}").append("\r\n");
		builder.append("\r\n");
		builder.append("\t").append("public ").append(javaName).append("() {").append("\r\n");
		builder.append("\t\t").append("super();").append("\r\n");
		builder.append("\t\t").append("builder = ").append(message.getName()).append(".newBuilder();").append("\r\n");
		builder.append("\t\t").append("messageId = MessageId.").append(message.getMessageIdName()).append("_VALUE;").append("\r\n");
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
		String voClassName = JavaFilesMaker.makeJavaClassName(className);
		boolean isJavaStruct = JavaFilesMaker.isJavaStruct(className);
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
			methodBuilder.append("\t").append("public ").append(checkName).append(" ").append(getter).append("() {").append("\r\n");
			methodBuilder.append("\t\t").append(checkName).append(" ret = ");
			if (isJavaStruct) {
				methodBuilder.append(param).append(".").append(getter).append("();").append("\r\n");
			} else {
				if (field.type.equals("optional")) {
					methodBuilder.append("null;").append("\r\n");
					methodBuilder.append("\t\t").append(className).append(" vo = this.").append(param).append(".").append(getter).append("();").append("\r\n");
					methodBuilder.append("\t\t").append("ret = vo == null ? null : new ").append(voClassName).append("(vo);").append("\r\n");
				} else {
					methodBuilder.append("new ").append(voClassName).append("(").append(param).append(".").append(getter).append("());").append("\r\n");
				}
			}
			methodBuilder.append("\t\t").append("return ret;").append("\r\n");
		} else if (field.type.equals("repeated")) {
			JavaFilesMaker.addImport(imports, importBuilder, List.class);
			if (isJavaStruct) {
				methodBuilder.append("\t").append("public List<").append(className).append("> ").append(getter).append("List() {").append("\r\n");
				methodBuilder.append("\t\t").append("return ").append(param).append(".").append(getter).append("List();").append("\r\n");
			} else {
				JavaFilesMaker.addImport(imports, importBuilder, Lists.class);
				methodBuilder.append("\t").append("public List<").append(voClassName).append("> ").append(getter).append("List() {").append("\r\n");
				methodBuilder.append("\t\t").append("List<").append(className).append("> list = ").append(param).append(".").append(getter).append("List();").append("\r\n");
				methodBuilder.append("\t\t").append("List<").append(voClassName).append("> ret = Lists.newArrayListWithCapacity(list.size());").append("\r\n");
				methodBuilder.append("\t\t").append("for (").append(className).append(" vo : list) {").append("\r\n");
				methodBuilder.append("\t\t\t").append("ret.add(new ").append(voClassName).append("(vo));").append("\r\n");
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
		String voClassName = JavaFilesMaker.makeJavaClassName(className);
		boolean isJavaStruct = JavaFilesMaker.isJavaStruct(className);
		if (field.type.equals("required") || field.type.equals("optional")) {
			if (field.type.equals("optional")) {
				paramBulider.append("\t\t").append("if (").append(field.name).append(" != null) {").append("\r\n");
				paramBulider.append("\t");
			}
			methodBuilder.append(voClassName).append(" ");
			paramBulider.append("\t\t").append("builder.set").append(JavaFilesMaker.firstUpper(field.name)).append("(").append(field.name);
			if (isJavaStruct) {
				paramBulider.append(");");
			} else {
				paramBulider.append(".get").append(className).append("());");
			}
			paramBulider.append("\r\n");
			if (field.type.equals("optional")) {
				paramBulider.append("\t\t").append("}").append("\r\n");
			}
		} else if (field.type.equals("repeated")) {
			methodBuilder.append("Iterable<").append(voClassName).append("> ");
			if (isJavaStruct) {
				paramBulider.append("\t\t").append("builder.addAll").append(JavaFilesMaker.firstUpper(field.name)).append("(").append(field.name).append(");").append("\r\n");
			} else {
				if (messages.containsKey(className)) {
					String importFile = messages.get(className).getProtoName();
					if (!imports.containsKey(importFile)) {
						imports.put(importFile, importFile);
						importBuilder.append(importPre).append(importFile).append("Protos.*;").append("\r\n");
					}
				}
				
				JavaFilesMaker.addImport(imports, importBuilder, Lists.class);
				paramBulider.append("\t\t").append("List<").append(className).append("> list = Lists.newLinkedList();").append("\r\n");
				paramBulider.append("\t\t").append("for (").append(voClassName).append(" vo : ").append(field.name).append(") {").append("\r\n");
				paramBulider.append("\t\t\t").append("list.add(vo.get").append(className).append("());").append("\r\n");
				paramBulider.append("\t\t").append("}").append("\r\n");
				paramBulider.append("\t\t").append("builder.addAll").append(JavaFilesMaker.firstUpper(field.name)).append("(list);").append("\r\n");
			}
		}
		methodBuilder.append(field.name).append(") {").append("\r\n");
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
