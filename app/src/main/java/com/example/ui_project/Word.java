package com.example.ui_project;

public class Word {


        private String term;
        private String definition;

        public Word(String term, String definition){
            this.term=term;
            this.definition=definition;
        }
        public String getTerm(){
            return term;
        }

        public String getDefinition() {
            return definition;
        }

        public void setTerm(String term) {
        this.term = term;

        }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}

