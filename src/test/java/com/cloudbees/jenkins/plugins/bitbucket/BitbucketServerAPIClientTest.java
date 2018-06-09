/*
 * The MIT License
 *
 * Copyright (c) 2018, Benjamin Fuchs
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
package com.cloudbees.jenkins.plugins.bitbucket;

import jenkins.scm.api.SCMFile;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.impl.mock.MockSCMRevision;
import com.cloudbees.jenkins.plugins.bitbucket.filesystem.BitbucketSCMFile;
import com.cloudbees.jenkins.plugins.bitbucket.filesystem.BitbucketSCMFileSystem;
import com.cloudbees.jenkins.plugins.bitbucket.mock.MockBitbucketSCMFileSystem;
import com.cloudbees.jenkins.plugins.bitbucket.mock.MockBitbucketServerAPIClient;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Before;

public class BitbucketServerAPIClientTest {

    private MockBitbucketServerAPIClient api;

    @Before
    public void setUp() throws IOException {
        api = new MockBitbucketServerAPIClient("http://localhost:1234", "test", null, null, false);
    }

    @Test
    public void testGetRefForPullRequestForMergeStrategy() throws Exception {
        PullRequestSCMHead head = new PullRequestSCMHead("foo", "", "", "", "", null, null,
                ChangeRequestCheckoutStrategy.MERGE);
        MockSCMRevision rev = new MockSCMRevision(head, null);
        BitbucketSCMFileSystem fileSystem = new MockBitbucketSCMFileSystem(null, null, (SCMRevision) rev);
        BitbucketSCMFile dir = new BitbucketSCMFile(fileSystem, null, "PR-123", null);
        assertEquals("refs/pull-requests/123/merge", api.getRefForPullRequest(dir));
    }

    @Test
    public void testGetRefForPullRequestForHeadStrategy() throws Exception {
        PullRequestSCMHead head = new PullRequestSCMHead("foo", "", "", "", "", null, null,
                ChangeRequestCheckoutStrategy.HEAD);
        MockSCMRevision rev = new MockSCMRevision(head, null);
        BitbucketSCMFileSystem fileSystem = new MockBitbucketSCMFileSystem(null, null, (SCMRevision) rev);
        BitbucketSCMFile dir = new BitbucketSCMFile(fileSystem, null, "PR-123", null);
        assertEquals("refs/pull-requests/123/from", api.getRefForPullRequest(dir));
    }

    @Test
    public void testGetRefForPullRequestWithFile() throws Exception {
        PullRequestSCMHead head = new PullRequestSCMHead("foo", "", "", "", "", null, null,
                ChangeRequestCheckoutStrategy.HEAD);
        MockSCMRevision rev = new MockSCMRevision(head, null);
        BitbucketSCMFileSystem fileSystem = new MockBitbucketSCMFileSystem(null, null, (SCMRevision) rev);
        BitbucketSCMFile dir = new BitbucketSCMFile(fileSystem, null, "PR-123", null);
        BitbucketSCMFile file = new BitbucketSCMFile(dir, "Jenkinsfile", SCMFile.Type.REGULAR_FILE, null);
        assertEquals("refs/pull-requests/123/from", api.getRefForPullRequest(file));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetRefForPullRequestFailsWithError() throws Exception {
        SCMHead head = new SCMHead("foo");
        MockSCMRevision rev = new MockSCMRevision(head, null);
        BitbucketSCMFileSystem fileSystem = new MockBitbucketSCMFileSystem(null, null, (SCMRevision) rev);
        BitbucketSCMFile dir = new BitbucketSCMFile(fileSystem, null, "PR-123", null);
        BitbucketSCMFile file = new BitbucketSCMFile(dir, "Jenkinsfile", SCMFile.Type.REGULAR_FILE, null);
        api.getRefForPullRequest(file);
    }
}
