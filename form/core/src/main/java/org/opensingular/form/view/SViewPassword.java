/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.view;

public class SViewPassword extends SView {


    /**
     * Flag indicating whether the contents of the field should be reset each time it is rendered.
     * <code>true</code> the contents are emptied when the field is rendered.
     * <code>false</code> the contents of the model are put into the field.
     */
    private boolean resetPassword = true;

    /**
     * Method for get the flag resetPassword.
     * @return Returns the resetPassword.
     */
    public final boolean getResetPassword() {
        return resetPassword;
    }

    /**
     * Method responsible to change the resetPassword attribute.
     * <code>true</code> The password will be cleaned in the request, so will not traffic in networking.
     * <code>false</code> The password will NOT be cleaned be careful about safety. Should not use in Login form's.
     * <b>If it's false don't means that will be save in the xml/database, by default never saves the password.</b>
     * @param resetPassword The resetPassword to set.
     * @return <code>this</code>.
     */
    public final SViewPassword setResetPassword(final boolean resetPassword) {
        this.resetPassword = resetPassword;
        return this;
    }
}
