package br.edu.ufersa.LibreFox.editora.entities;

    public class Endereco {
        private long id;
        private String numero;
        private String bairro;
        private String Logradouro;
        private String cidade;
        private String uf;

        public Endereco(String numero, String bairro, String Logradouro, String cidade, String uf) {

        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            if (id >= 0) {
                this.id = id;
            }
            else {
                throw new RuntimeException("Id não deve ser vazio ou negativo");
            }
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            if (!numero.isBlank()) {
                this.numero = numero;
            }
            else {
                throw new RuntimeException("O campo número não pode ser vazio");
            }
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            if (!bairro.isBlank()) {
                this.bairro = bairro;
            }
            else {
                throw new RuntimeException("O campo bairro não pode ser vazio");
            }
        }

        public String getLogradouro() {
            return Logradouro;
        }

        public void setLogradouro(String logradouro) {
            if (!logradouro.isBlank()) {
                this.Logradouro = logradouro;
            }
            else {
                throw new RuntimeException("O campo logradouro não pode ser vazio");
            }
        }
        public String getCidade() {
            return cidade;
        }

        public void setCidade(String cidade) {
            if (!cidade.isBlank()) {
                this.cidade = cidade;
            } else {
                throw new RuntimeException("O campo cidade não pode ser vazio");
            }
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            if (!uf.isBlank()) {
                this.uf = uf;
            }
            else {
                throw new RuntimeException("O campo UF não pode ser vazio");
            }
        }

    }
