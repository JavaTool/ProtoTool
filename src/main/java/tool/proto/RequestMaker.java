package tool.proto;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class RequestMaker {
	
	private final Map<String, ProtoMessage> messages;
	
	public RequestMaker(Map<String, ProtoMessage> messages) {
		this.messages = messages;
	}
	
	public void output(String dir, String path, String protoPath) {
		try {
			for (ProtoMessage message : messages.values()) {
				if (message.isServerProcessor()) {
					outputRequests(message, dir, path, protoPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void outputRequests(ProtoMessage message, String dir, String path, String protoPath) throws Exception {
		if (message.getFields().size() > 0) {
			String[] names = message.getName().split("_");
			StringBuilder nameBuilder = new StringBuilder();
			for (String nam : names) {
				nameBuilder.append(JavaFilesMaker.firstUpper(nam.toLowerCase()));
			}
			String name = nameBuilder.toString();
			String javaName = name + "Request";
			File java = new File(JavaFilesMaker.checkString(dir) ? dir : "../../../CrossGateBase/src");
			java = new File(java, path);
			java.mkdirs();
			java = JavaFilesMaker.createFile(java, javaName + ".java");
			String importPre = "import " + protoPath.replaceAll("/", ".") + ".";
			
			StringBuilder builder = new StringBuilder("package "), elementBuilder = new StringBuilder("\r\n");
			builder.append(path.replaceAll("/", ".")).append(";").append("\r\n");
			builder.append("\r\n");
			String param = JavaFilesMaker.firstLower(name);
			String paramDefine = message.getName() + " " + param;
			elementBuilder.append("\t").append("private ").append(paramDefine).append(";").append("\r\n");
			StringBuilder importBuilder = new StringBuilder(), methodBuilder = new StringBuilder(), annotateBuilder = new StringBuilder();
			StringBuilder methodsBuilder = new StringBuilder();
			importBuilder.append("import java.io.InputStream;").append("\r\n");
			importBuilder.append("\r\n");
			importBuilder.append("import net.dipatch.ISender;").append("\r\n");
			importBuilder.append("import net.io.protocal.proto.ProtoRequest;").append("\r\n");
			importBuilder.append(importPre).append(message.getProtoName()).append("Protos.*;");
			Map<String, String> imports = Maps.newHashMap();
			
			for (ProtoMessageField field : message.getFields().values()) {
				String className = JavaFilesMaker.getProtoClassName(field.className);
				if (messages.containsKey(className)) {
					String importFile = messages.get(className).getProtoName();
					if (!imports.containsKey(importFile)) {
						imports.put(importFile, importFile);
						importBuilder.append("\r\n");
						importBuilder.append(importPre).append(importFile).append("Protos.*;");
					}
				}
				
				annotateBuilder.setLength(0);
				annotateBuilder.append("\t").append("/**").append("\r\n");
				annotateBuilder.append("\t").append(" * ").append("\r\n");
				annotateBuilder.append("\t").append(" * @return").append("\t").append(field.getAnnotate()).append("\r\n");
				annotateBuilder.append("\t").append(" */").append("\r\n");
				
				methodBuilder.setLength(0);
				String getter = "get" + JavaFilesMaker.firstUpper(field.name);
				if (field.type.equals("required") || field.type.equals("optional")) {
					methodBuilder.append("\t").append("public ").append(className).append(" ").append(getter).append("() {").append("\r\n");
					methodBuilder.append("\t\t").append("return ").append(param).append(".").append(getter).append("();").append("\r\n");
				} else if (field.type.equals("repeated")) {
					JavaFilesMaker.addImport(imports, importBuilder, List.class);
					methodBuilder.append("\t").append("public List<").append(className).append("> ").append(getter).append("List() {").append("\r\n");
					methodBuilder.append("\t\t").append("return ").append(param).append(".").append(getter).append("List();").append("\r\n");
				}
				methodBuilder.append("\t").append("}").append("\r\n");
				methodBuilder.append("\r\n");
				
				methodsBuilder.append(annotateBuilder.toString()).append(methodBuilder.toString());
			}
			
			methodsBuilder.append("\t").append("public ").append(message.getName()).append(" get").append(name).append("() {").append("\r\n");
			methodsBuilder.append("\t\t").append("return ").append(param).append(";").append("\r\n");
			methodsBuilder.append("\t").append("}").append("\r\n");
			methodsBuilder.append("\r\n");
	
			methodsBuilder.append("\t").append("@Override").append("\r\n");
			methodsBuilder.append("\t").append("public byte[] getByteArray() {").append("\r\n");
			methodsBuilder.append("\t\t").append("return ").append(param).append(".toByteArray();").append("\r\n");
			methodsBuilder.append("\t").append("}").append("\r\n");
			
			builder.append(importBuilder.toString()).append("\r\n");
			builder.append("\r\n");
			builder.append("/**").append("\r\n");
			builder.append(" * This is a auto make java file, so do not modify me.").append("\r\n");
			builder.append(" * @author fuhuiyuan").append("\r\n");
			builder.append(" */").append("\r\n");
			builder.append("public class ").append(javaName).append(" extends ProtoRequest {").append("\r\n");
			builder.append(elementBuilder.toString());
			builder.append("\r\n");
			builder.append("\t").append("public ").append(javaName).append("(int receiveMessageId, byte[] datas, ISender sender, String sessionId, ").append(paramDefine).append(") {").append("\r\n");
			builder.append("\t\t").append("super(receiveMessageId, datas, sender, sessionId);").append("\r\n");
			builder.append("\t\t").append("this.").append(param).append(" = ").append(param).append(";").append("\r\n");
			builder.append("\t").append("}").append("\r\n");
			builder.append("\r\n");
			builder.append("\t").append("public ").append(javaName).append("(int receiveMessageId, InputStream is, int contentLength, ISender sender, String sessionId, ").append(paramDefine).append(") throws Exception {").append("\r\n");
			builder.append("\t\t").append("super(receiveMessageId, is, contentLength, sender, sessionId);").append("\r\n");
			builder.append("\t\t").append("this.").append(param).append(" = ").append(param).append(";").append("\r\n");
			builder.append("\t").append("}").append("\r\n");
			builder.append("\r\n");
			builder.append(methodsBuilder.toString()).append("\r\n");
			builder.append("}").append("\r\n");
			
			FileWriter writer = new FileWriter(java);
			writer.append(builder.toString());
			writer.flush();
			writer.close();
		}
	}

}
