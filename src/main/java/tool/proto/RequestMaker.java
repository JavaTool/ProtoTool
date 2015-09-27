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
	
	public void output(String dir, String path) {
		try {
			for (ProtoMessage message : messages.values()) {
				if (message.isServerProcessor()) {
					outputRequests(message, dir, path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void outputRequests(ProtoMessage message, String dir, String path) throws Exception {
		if (message.getFields().size() > 0) {
			String javaName = message.getName() + "Request";
			File java = new File(JavaFilesMaker.checkString(dir) ? dir : "../../../CrossGateBase/src");
			java = new File(java, path);
			java.mkdirs();
			java = JavaFilesMaker.createFile(java, javaName + ".java");
			String importPre = "import " + path.replace("/", ".");
			
			StringBuilder builder = new StringBuilder("package "), elementBuilder = new StringBuilder("\r\n");
			builder.append(path.replaceAll("/", ".")).append(";").append("\r\n");
			String param = JavaFilesMaker.firstLower(message.getName());
			String paramDefine = message.getName() + " " + param;
			elementBuilder.append("\t").append("private ").append(paramDefine).append(";").append("\r\n");
			StringBuilder importBuilder = new StringBuilder(), methodBuilder = new StringBuilder(), annotateBuilder = new StringBuilder();
			StringBuilder methodsBuilder = new StringBuilder();
			importBuilder.append(importPre).append(".Request;").append("\r\n");
			importBuilder.append("import javax.servlet.http.HttpSession;").append("\r\n");
			importBuilder.append("import net.dipatch.ISender;").append("\r\n");
			importBuilder.append(importPre).append(".protoBuf.").append(message.getProtoName()).append("Protos.*;");
			Map<String, String> imports = Maps.newHashMap();
			
			for (ProtoMessageField field : message.getFields().values()) {
				String className = JavaFilesMaker.getProtoClassName(field.className);
				
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
				methodBuilder.append("\t").append("}");
				methodBuilder.append("\r\n");
				
				methodsBuilder.append(annotateBuilder.toString()).append(methodBuilder.toString());
			}
			
			methodsBuilder.append("\r\n");
			methodsBuilder.append("\t").append("public ").append(message.getName()).append(" get").append(message.getName()).append("() {").append("\r\n");
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
			builder.append("public class ").append(javaName).append(" extends Request {").append("\r\n");
			builder.append(elementBuilder.toString());
			builder.append("\r\n");
			builder.append("\t").append("public ").append(javaName).append("(int receiveMessageId, byte[] datas, ISender sender, String sessionId, ").append(paramDefine).append(") {").append("\r\n");
			builder.append("\t\t").append("super(receiveMessageId, datas, sender, sessionId);").append("\r\n");
			builder.append("\t\t").append("this.").append(param).append(" = ").append(param).append(";").append("\r\n");
			builder.append("\t").append("}").append("\r\n");
			builder.append("\r\n");
			builder.append("\t").append("public ").append(javaName).append("(Request request, ").append(paramDefine).append(") {").append("\r\n");
			builder.append("\t\t").append("super(request);").append("\r\n");
			builder.append("\t\t").append("this.").append(param).append(" = ").append(param).append(";").append("\r\n");
			builder.append("\t").append("}").append("\r\n");
			builder.append(methodsBuilder.toString()).append("\r\n");
			builder.append("}").append("\r\n");
			
			FileWriter writer = new FileWriter(java);
			writer.append(builder.toString());
			writer.flush();
			writer.close();
		}
	}

}
