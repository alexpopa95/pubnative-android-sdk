// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.demo;

import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.model.PubnativeAdModel;

public class CellRequestModel {

    private static final String TAG = CellRequestModel.class.getSimpleName();
    public PubnativeNetworkRequest request;
    public PubnativeAdModel        adModel;
    public String                  placementID;

    public CellRequestModel(String placementID) {

        request = new PubnativeNetworkRequest();
        request.addKeyword("game");
        request.addKeyword("social");
        request.setGender(PubnativeNetworkRequest.Gender.MALE);
        request.setParameter("icon_size", "50x50");
        this.placementID = placementID;
    }

    @Override
    public int hashCode() {
        return placementID.hashCode();
    }
}
