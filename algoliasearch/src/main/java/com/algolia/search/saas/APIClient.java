/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.algolia.search.saas;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.algolia.search.saas.listeners.APIClientListener;

import org.json.JSONArray;

import java.util.List;

/**
 * Entry point in the Java API.
 * You should instantiate a Client object with your ApplicationID, ApiKey and Hosts
 * to start using Algolia Search API
 */
public class APIClient extends BaseAPIClient {
    /**
     * Algolia Search initialization
     * @param applicationID the application ID you have in your admin interface
     * @param apiKey a valid API key for the service
     */
    public APIClient(@NonNull String applicationID, @NonNull String apiKey) {
        this(applicationID, apiKey, null);
    }

    /**
     * Algolia Search initialization
     * @param applicationID the application ID you have in your admin interface
     * @param apiKey a valid API key for the service
     * @param hosts the list of hosts that you have received for the service
     */
    public APIClient(@NonNull String applicationID, @NonNull String apiKey, String[] hosts) {
        super(applicationID, apiKey, hosts);
    }

    /**
     * Get the index object initialized (no server call needed for initialization)
     *
     * @param indexName the name of index
     */
    public Index initIndex(@NonNull String indexName) {
        return new Index(this, indexName);
    }

    private class ASyncClientTask extends AsyncTask<TaskParams.Client, Void, TaskParams.Client> {
        @Override
        protected TaskParams.Client doInBackground(TaskParams.Client... params) {
            TaskParams.Client p = params[0];
            try {
                switch (p.method) {
                    case ListIndexes:
                        p.content = listIndexes();
                        break;
                    case DeleteIndex:
                        p.content = deleteIndex(p.indexName);
                        break;
                    case MoveIndex:
                        p.content = moveIndex(p.srcIndexName, p.dstIndexName);
                        break;
                    case CopyIndex:
                        p.content = copyIndex(p.srcIndexName, p.dstIndexName);
                        break;
                    case MultipleQueries:
                        p.content = multipleQueries(p.queries, p.strategy);
                        break;
                    case Batch:
                        p.content = batch(p.actions);
                        break;
                }
            } catch (AlgoliaException e) {
                p.error = e;
            }

            return p;
        }

        @Override
        protected void onPostExecute(TaskParams.Client p) {
            p.sendResult(APIClient.this);
        }
    }

    /**
     * List all existing user keys with their associated ACLs
     *
     * @param listener the listener that will receive the result or error.
     * @return A cancellable request.
     */
    public Request listIndexesASync(APIClientListener listener) {
        TaskParams.Client params = new TaskParams.Client(listener, APIMethod.ListIndexes);
        return new Request(new ASyncClientTask().execute(params));
    }

    /**
     * Delete an index
     *
     * @param indexName the name of index to delete
     * @return A cancellable request.
     */
    public Request deleteIndexASync(String indexName, APIClientListener listener) {
        TaskParams.Client params = new TaskParams.Client(listener, APIMethod.DeleteIndex, indexName);
        return new Request(new ASyncClientTask().execute(params));
    }

    /**
     * Move an existing index.
     * @param srcIndexName the name of index to copy.
     * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
     * @return A cancellable request.
     */
    public Request moveIndexASync(String srcIndexName, String dstIndexName, APIClientListener listener) {
        TaskParams.Client params = new TaskParams.Client(listener, APIMethod.MoveIndex, srcIndexName, dstIndexName);
        return new Request(new ASyncClientTask().execute(params));
    }

    /**
     * Copy an existing index.
     * @param srcIndexName the name of index to copy.
     * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist).
     * @return A cancellable request.
     */
    public Request copyIndexASync(String srcIndexName, String dstIndexName, APIClientListener listener) {
        TaskParams.Client params = new TaskParams.Client(listener, APIMethod.CopyIndex, srcIndexName, dstIndexName);
        return new Request(new ASyncClientTask().execute(params));
    }

    /**
     * This method allows to query multiple indexes with one API call asynchronously
     * @return A cancellable request.
     */
    public Request multipleQueriesASync(List<IndexQuery> queries, APIClientListener listener) {
        TaskParams.Client params = new TaskParams.Client(listener, APIMethod.MultipleQueries, queries, "none");
        return new Request(new ASyncClientTask().execute(params));
    }

    /**
     * This method allows to query multiple indexes with one API call asynchronously
     * @return A cancellable request.
     */
    public Request multipleQueriesASync(List<IndexQuery> queries, String strategy, APIClientListener listener) {
        TaskParams.Client params = new TaskParams.Client(listener, APIMethod.MultipleQueries, queries, strategy);
        return new Request(new ASyncClientTask().execute(params));
    }

    /**
     * Custom batch asynchronous
     *
     * @param actions the array of actions
     * @return A cancellable request.
     */
    public Request batchASync(JSONArray actions, APIClientListener listener) {
        TaskParams.Client params = new TaskParams.Client(listener, APIMethod.Batch, actions);
        return new Request(new ASyncClientTask().execute(params));
    }
}