if(! window.FileUploadPanel){
    window.FileUploadPanel = function(){};
    window.FileUploadPanel.validateInputFile = function(input, maxSize, erase){
        if( input.files[0].size  > maxSize) {
            toastr.error("Arquivo não pode ser maior que "+FileUploadPanel.humaneSize(maxSize));
            FileUploadPanel.resetFormElement(input);
            return false;
        }
        return true;
    };

    window.FileUploadPanel.resetFormElement = function(e) {
        e.wrap('<form>').closest('form').get(0).reset();
        e.unwrap();

        // Prevent form submission
        e.stopPropagation();
        e.preventDefault();
    }

    window.FileUploadPanel.humaneSize = function(size){
        var remainder = size;
        var index = 0;
        var names = ['bytes', 'KB', 'MB', 'GB', 'TB'];
        while(remainder >= 1024){
            remainder /= 1024; index ++;
        }
        return remainder +" "+ names[index];
    }
}