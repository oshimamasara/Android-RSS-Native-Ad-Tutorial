/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oshimamasara.myrssappad;

class MenuItem {
    private final String title;
    private final String pubDate;
    private final String description;
    private final String link;

    public MenuItem(String title,String pubDate, String description, String link) {
        this.title = title;
        this.pubDate=pubDate;
        this.description = description;
        this.link=link;
    }

    public String getTitle(){
        return title;
    }

    public String getPubDate(){
        return pubDate;
    }

    public String getDescription(){
        return description;
    }

    public String getLink(){
        return link;
    }
}
