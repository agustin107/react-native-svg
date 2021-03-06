import React from "react";
import PropTypes from "prop-types";
import { Image, requireNativeComponent } from "react-native";
import ImageSourcePropType from "react-native/Libraries/Image/ImageSourcePropType";
import { ImageAttributes } from "../lib/attributes";
import { numberProp, touchableProps, responderProps } from "../lib/props";
import Shape from "./Shape";
import { meetOrSliceTypes, alignEnum } from "../lib/extract/extractViewBox";
import extractProps from "../lib/extract/extractProps";

const spacesRegExp = /\s+/;

export default class extends Shape {
    static displayName = "Image";
    static propTypes = {
        ...responderProps,
        ...touchableProps,
        x: numberProp,
        y: numberProp,
        width: numberProp.isRequired,
        height: numberProp.isRequired,
        href: ImageSourcePropType,
        preserveAspectRatio: PropTypes.string,
    };

    static defaultProps = {
        x: 0,
        y: 0,
        width: 0,
        height: 0,
        preserveAspectRatio: "xMidYMid meet",
    };

    setNativeProps = (props) => {
        if (props.width) {
            props.imagewidth = `${props.width}`;
        }
        if (props.height) {
            props.imageheight = `${props.height}`;
        }
        this.root.setNativeProps(props);
    };

    render() {
        let { props } = this;
        let modes = props.preserveAspectRatio.trim().split(spacesRegExp);
        let meetOrSlice = meetOrSliceTypes[modes[1]] || 0;
        let align = alignEnum[modes[0]] || "xMidYMid";

        return (
            <RNSVGImage
                ref={ele => {
                    this.root = ele;
                }}
                {...extractProps({ ...props, x: null, y: null }, this)}
                x={props.x.toString()}
                y={props.y.toString()}
                imagewidth={props.width.toString()}
                imageheight={props.height.toString()}
                meetOrSlice={meetOrSlice}
                align={align}
                src={Image.resolveAssetSource(props.href)}
            />
        );
    }
}

const RNSVGImage = requireNativeComponent("RNSVGImage", null, {
    nativeOnly: ImageAttributes,
});
