/**
 *
 * Copyright (c) 2014, the Railo Company Ltd. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
/*
*

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package lucee.runtime.img.filter;import java.awt.image.BufferedImage;

import lucee.runtime.engine.ThreadLocalPageContext;
import lucee.runtime.exp.FunctionException;
import lucee.runtime.exp.PageException;
import lucee.runtime.img.ImageUtil;
import lucee.runtime.type.KeyImpl;
import lucee.runtime.type.Struct;
import lucee.runtime.type.util.CollectionUtil;



/**
 * A filter which changes the gain and bias of an image - similar to ContrastFilter.
 */
public class GainFilter extends TransferFilter  implements DynFiltering {

	private float gain = 0.5f;
	private float bias = 0.5f;
	
	@Override
	protected float transferFunction( float f ) {
		f = ImageMath.gain(f, gain);
		f = ImageMath.bias(f, bias);
		return f;
	}

    /**
     * Set the gain.
     * @param gain the gain
     * @min-value: 0
     * @max-value: 1
     * @see #getGain
     */
	public void setGain(float gain) {
		this.gain = gain;
		initialized = false;
	}
	
    /**
     * Get the gain.
     * @return the gain
     * @see #setGain
     */
	public float getGain() {
		return gain;
	}

    /**
     * Set the bias.
     * @param bias the bias
     * @min-value: 0
     * @max-value: 1
     * @see #getBias
     */
	public void setBias(float bias) {
		this.bias = bias;
		initialized = false;
	}
	
    /**
     * Get the bias.
     * @return the bias
     * @see #setBias
     */
	public float getBias() {
		return bias;
	}

	@Override
	public String toString() {
		return "Colors/Gain...";
	}

	@Override
	public BufferedImage filter(BufferedImage src, Struct parameters) throws PageException {BufferedImage dst=ImageUtil.createBufferedImage(src);
		Object o;
		if((o=parameters.removeEL(KeyImpl.init("Gain")))!=null)setGain(ImageFilterUtil.toFloatValue(o,"Gain"));
		if((o=parameters.removeEL(KeyImpl.init("Bias")))!=null)setBias(ImageFilterUtil.toFloatValue(o,"Bias"));
		if((o=parameters.removeEL(KeyImpl.init("Dimensions")))!=null){
			int[] dim=ImageFilterUtil.toDimensions(o,"Dimensions");
			setDimensions(dim[0],dim[1]);
		}

		// check for arguments not supported
		if(parameters.size()>0) {
			throw new FunctionException(ThreadLocalPageContext.get(), "ImageFilter", 3, "parameters", "the parameter"+(parameters.size()>1?"s":"")+" ["+CollectionUtil.getKeyList(parameters,", ")+"] "+(parameters.size()>1?"are":"is")+" not allowed, only the following parameters are supported [Gain, Bias, Dimensions]");
		}

		return filter(src, dst);
	}
}