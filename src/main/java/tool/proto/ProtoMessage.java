package tool.proto;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProtoMessage {
	
	private String messageIdName;
	
	private int messageIdValue;
	
	private Map<String, ProtoMessageField> fields = Maps.newHashMap();
	
	private String head;
	
	private String protoName;
	
	private List<String> imports = Lists.newLinkedList();
	
	private boolean isEnum;
	
	private boolean isReturn;

	public String getMessageIdName() {
		return messageIdName;
	}

	public void setMessageIdName(String messageIdName) {
		this.messageIdName = messageIdName;
	}

	public int getMessageIdValue() {
		return messageIdValue;
	}

	public void setMessageIdValue(int messageIdValue) {
		this.messageIdValue = messageIdValue;
	}
	
	public void addField(ProtoMessageField protoMessageField) {
		fields.put(protoMessageField.name, protoMessageField);
	}

	public void setFields(Map<String, ProtoMessageField> fields) {
		this.fields = fields;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getProtoName() {
		return protoName;
	}

	public void setProtoName(String protoName) {
		this.protoName = protoName;
	}
	
	public void putImport(String value) {
		imports.add(value);
	}
	
	public List<String> getImport() {
		return imports;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public boolean isReturn() {
		return isReturn;
	}

	public void setReturn(boolean isReturn) {
		this.isReturn = isReturn;
	}

}
