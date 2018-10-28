package com.cbre.tsandford.cbreinspector.misc.camera;

// manages height and width that gets set in Camera.Parameters

public class CameraSettings {

    public enum PictureRatio{
        four,       // 4:3
        sixteen     // 16:9
    }

    public enum Orientation{
        Portrait,
        Landscape
    }
    private int _jpeg_quality;

    private int _base_size; // initially represents width, swaps to height when orientation changes
    private static final int STANDARD_WIDTH = 4032;

    private PictureRatio _ratio;
    private Orientation _orientation;

    private int _pic_width;
    private int _pic_height;

    public CameraSettings(int quality, int base_width){
        this._jpeg_quality = quality;
        this._base_size = base_width;

        // starting defaults
        this._orientation = Orientation.Landscape;
        this._ratio = PictureRatio.four;

        // inferred starting sizes
        set_height_and_width();
    }

    public CameraSettings(int quality){
        this._jpeg_quality = quality;
        this._base_size = STANDARD_WIDTH;

        // starting defaults
        this._orientation = Orientation.Landscape;
        this._ratio = PictureRatio.four;

        // inferred starting sizes
        set_height_and_width();
    }

    // region Properties

    // Ratio - read-write
    public void set_ratio(PictureRatio ratio){
        this._ratio = ratio;
        set_height_and_width();
    }
    public PictureRatio get_ratio(){
        return this._ratio;
    }

    // Base size - read-write
    public void set_base_size(int _base_size) {
        this._base_size = _base_size;
        set_height_and_width();
    }
    public int get_base_size() {
        return _base_size;
    }

    // Orientation - read-write
    public Orientation get_orientation() {
        return _orientation;
    }
    public void set_orientation(Orientation _orientation) {
        this._orientation = _orientation;
        set_height_and_width();
    }

    // Quality - read-write
    public void set_jpeg_quality(int _jpeg_quality) {
        this._jpeg_quality = _jpeg_quality;
    }
    public int get_jpeg_quality() {
        return _jpeg_quality;
    }

    // Width and Height - read-only
    public int get_pic_width() {
        return _pic_width;
    }
    public int get_pic_height() {
        return _pic_height;
    }

    //endregion

    // region Public Methods

    public boolean test_methods(){
        /*
        Base Size	4032

        O	L	    L	    P	    P
        R1	4	    16	    3	    9
        R2	3	    9	    4	    16

        W	4032	4032	3024	2268
        H	3024	2268	4032	4032

         */
        int TEST_BASE = 4032;
        int result;

        PictureRatio TEST_RATIO;
        Orientation TEST_ORIENTATION;



        TEST_ORIENTATION = Orientation.Landscape;

        TEST_RATIO = PictureRatio.four;
        // height of landscape at 4:3
        result = calculate_height(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 3024) return false;

        // width of landscape at 4:3
        result = calculate_width(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 4032) return false;

        TEST_RATIO = PictureRatio.sixteen;
        // height of landscape at 16:9
        result = calculate_height(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 2268) return false;

        // width of landscape at 16:9
        result = calculate_width(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 4032) return false;




        TEST_ORIENTATION = Orientation.Portrait;

        TEST_RATIO = PictureRatio.four;
        // height of Portrait at 4:3
        result = calculate_height(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 4032) return false;

        // width of Portrait at 4:3
        result = calculate_width(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 3024) return false;

        TEST_RATIO = PictureRatio.sixteen;
        // height of Portrait at 16:9
        result = calculate_height(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 4032) return false;

        // width of Portrait at 16:9
        result = calculate_width(TEST_ORIENTATION, TEST_BASE, TEST_RATIO);
        if(result != 2268) return false;

        return true;

    }

    // endregion

    // region Private Methods

    private void set_height_and_width(){
        this._pic_height = calculate_height(this._orientation,
                this._base_size,
                this._ratio);

        this._pic_width = calculate_width(this._orientation,
                this._base_size,
                this._ratio);
    }

    private int calculate_height(Orientation orientation,
                                 int base_size,
                                 PictureRatio ratio){
        switch(orientation){
            case Landscape:
                if(ratio == PictureRatio.four)
                    return base_size / 4 * 3;
                else if(ratio == PictureRatio.sixteen)
                    return base_size / 16 * 9;
            case Portrait:
                return base_size;
        }
        return -1;
    }


    private int calculate_width(Orientation orientation,
                                int base_size,
                                PictureRatio ratio) {
        switch(orientation){
            case Landscape:
                return base_size;
            case Portrait:
                if(ratio == PictureRatio.four)
                    return base_size / 4 * 3;
                else if(ratio == PictureRatio.sixteen)
                    return base_size / 16 * 9;
        }
        return -1;
    }

    // endregion

}
