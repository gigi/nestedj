/* 
 * The MIT License
 *
 * Copyright 2015 exsio.
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
package pl.exsio.nestedj;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.exsio.nestedj.model.TestNodeImpl;
import pl.exsio.nestedj.repository.NestedNodeRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class FunctionalNestedjTest {

    @Autowired
    protected NestedNodeRepositoryImpl<TestNodeImpl> nodeRepository;

    @PersistenceContext
    protected EntityManager em;

    /**
     * STARTING NESTED TREE CONDITIONS
     * <p/>
     *                  1 A 16
     *                   / \                    IDS:
     *                  /   \                   A: 1
     *                 /     \                  B: 2
     *              2 B 7   8 C 15              C: 3
     *               /         \                D: 4
     *              /\         /\               E: 5
     *             /  \       /  \              F: 6
     *            /    \     /    \             G: 7
     *           /   5 E 6  9 F 10 \            H: 8
     *        3 D 4             11 G 14
     *                              \
     *                               \
     *                             12 H 13
     */
    protected TestNodeImpl findNode(String symbol) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TestNodeImpl> select = cb.createQuery(TestNodeImpl.class);
        Root<TestNodeImpl> root = select.from(TestNodeImpl.class);
        select.where(cb.equal(root.get("name"), symbol));
        TestNodeImpl n = em.createQuery(select).getSingleResult();
        printNode(symbol, n);
        this.em.refresh(n);
        return n;
    }

    protected void printNode(String symbol, TestNodeImpl n) {
        if(n != null) {
            System.out.println(String.format("Node %s: %d/%d/%d", symbol, n.getLeft(), n.getRight(), n.getLevel()));
        }
    }

    protected TestNodeImpl createTestNode(String symbol) {

        TestNodeImpl n = new TestNodeImpl();
        n.setName(symbol);
        n.setDiscriminator("tree_1");
        return n;
    }

    protected TestNodeImpl getParent(TestNodeImpl f) {
        this.em.refresh(f);
        TestNodeImpl parent = f.getParent();
        if (parent != null) {
            this.em.refresh(parent);
        }
        System.out.println(String.format("Parent of %s is %s", f.getName(), parent != null ? parent.getName() : "null"));
        return parent;
    }

}
