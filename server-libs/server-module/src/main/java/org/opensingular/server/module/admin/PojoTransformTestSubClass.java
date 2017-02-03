package org.opensingular.server.module.admin;

public class PojoTransformTestSubClass {
	private String nome;
	private String cpf;
	
	private Integer valorQualquer;
	
	private PojoTransformTestSuperClass pai;
	
	public PojoTransformTestSubClass() {
	}
	
	public PojoTransformTestSubClass(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Integer getValorQualquer() {
		return valorQualquer;
	}

	public void setValorQualquer(Integer valorQualquer) {
		this.valorQualquer = valorQualquer;
	}

	public PojoTransformTestSuperClass getPai() {
		return pai;
	}

	public void setPai(PojoTransformTestSuperClass pai) {
		this.pai = pai;
	}
	
}
