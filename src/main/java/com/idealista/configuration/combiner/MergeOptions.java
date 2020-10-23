package com.idealista.configuration.combiner;

public class MergeOptions {

    final boolean isAdditionAllowed;
    
    final boolean isModificationAllowed;

    final boolean isDeletionAllowed;

    final boolean removeBaseEscapes;

    private MergeOptions(boolean isAdditionAllowed, boolean isModificationAllowed, boolean isDeletionAllowed, boolean removeBaseEscapes) {
        this.isAdditionAllowed = isAdditionAllowed;
        this.isModificationAllowed = isModificationAllowed;
        this.isDeletionAllowed = isDeletionAllowed;
        this.removeBaseEscapes = removeBaseEscapes;
    }
    
    public static class Builder {
        
        private boolean isAdditionAllowed;
        
        private boolean isModificationAllowed;
        
        private boolean isDeletionAllowed;

        private boolean removeBaseEscapes;
        
        public Builder withAdditionAllowed() {
            this.isAdditionAllowed = true;
            return this;
        }
        
        public Builder withModificationAllowed() {
            this.isModificationAllowed = true;
            return this;
        }
        
        public Builder withDeletionAllowed() {
            this.isDeletionAllowed = true;
            return this;
        }

        public Builder withRemoveBaseEscapes() {
            this.removeBaseEscapes = true;
            return this;
        }
        
        public MergeOptions build() {
            return new MergeOptions(isAdditionAllowed, isModificationAllowed, isDeletionAllowed, removeBaseEscapes);
        }
        
    }
    
}